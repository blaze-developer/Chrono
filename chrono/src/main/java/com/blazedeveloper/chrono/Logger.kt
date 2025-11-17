package com.blazedeveloper.chrono

import com.blazedeveloper.chrono.dataflow.LogReceiver
import com.blazedeveloper.chrono.dataflow.ReplaySource
import com.blazedeveloper.chrono.structure.LogTable
import com.blazedeveloper.chrono.structure.LoggableInputs
import kotlin.system.exitProcess
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource.Monotonic
import kotlin.time.measureTime

object Logger {
    private val table: LogTable = LogTable()
    private val logReceivers = mutableListOf<LogReceiver>()
    private val metadataPairs = mutableListOf<Pair<String, String>>()

    var replaySource: ReplaySource? = null
    val hasReplaySource: Boolean get() = replaySource != null

    private val outputTable by lazy {
        table.subtable(if (!hasReplaySource) "RealOutputs" else "ReplayOutputs")
    }

    private val timings by lazy { outputTable.subtable("LoggerTimings") }

    private val loggerStart by lazy { Monotonic.markNow() }
    private lateinit var cycleStart: TimeMark
    private lateinit var timeBeforeUser: TimeMark

    fun interface Addable<T> {
        fun add(toAdd: T)
        operator fun plusAssign(toAdd: T) = add(toAdd)
        operator fun String.invoke(value: String) = Pair(this, value)
    }

    /**
     * The synchronized timestamp of the current cycle,
     * this should be used for all replayed logic as it is deterministic and replayable.
     **/
    val timestamp: Duration get() = table.timestamp

    /**
     * Object that user can add log receivers to that accept log
     * data from the Logger and use for streaming, logfiles, etc.
     */
    val receivers = Addable<LogReceiver> { logReceivers += it }

    /**
     * Maps log metadata names to values to be put into the table when
     * the Logger is started.
     */
    val metadata = Addable<Pair<String, String>> { metadataPairs += it }

    /** Starts the Logger, its receivers, and sources. */
    fun start() {
        val metadataTable = table.subtable(
            if (!hasReplaySource) "RealMetadata"
            else "ReplayMetadata"
        )

        metadataPairs.forEach { (key, value) -> metadataTable.put(key, value) }

        logReceivers.forEach { it.start() }

        replaySource?.start()
    }

    /** Stops the Logger, its receivers, and sources.*/
    fun stop() {
        logReceivers.forEach { it.stop() }

        replaySource?.stop()
    }

    /** Sets up the table for this cycle. Runs before user code. **/
    fun preUser() {
        cycleStart = Monotonic.markNow()


        if (hasReplaySource) {
            // Update table from the replay source, end if the source ends.
            val tableReadTime = measureTime {
                val updated = replaySource?.updateTable(table) ?: false
                if (!updated) {
                    exitProcess(0)
                }
            }
            timings.put("TableReadNS", tableReadTime.inWholeNanoseconds)
        } else {
            table.timestamp = loggerStart.elapsedNow()
        }

        timeBeforeUser = Monotonic.markNow()
    }

    /** Processes an input for this loop, either logging or replaying from the table. **/
    fun processInputs(subtableName: String, inputs: LoggableInputs) {
        if(hasReplaySource) {
            inputs.fromLog(table.subtable(subtableName))
        } else {
            inputs.toLog(table.subtable(subtableName))
        }
    }

    /** Sends data to receivers. Runs after user code. **/
    fun postUser() {
        val userCodeTime = timeBeforeUser.elapsedNow()
        timings.put("UserCodeNS", userCodeTime.inWholeNanoseconds)

        // Record Timings
        val fullCycleTime = cycleStart.elapsedNow()
        val loggerCycleTime = fullCycleTime - userCodeTime
        timings.put("FullCycleNS", fullCycleTime.inWholeNanoseconds)
        timings.put("LoggerCycleNS", loggerCycleTime.inWholeNanoseconds)

        val tableToReceive = table.clone()
        logReceivers.forEach { it.receive(tableToReceive) }
    }

    fun output(key: String, value: String) = outputTable.put(key, value)
    fun output(key: String, value: Boolean) = outputTable.put(key, value)
    fun output(key: String, value: Int) = outputTable.put(key, value)
    fun output(key: String, value: Long) = outputTable.put(key, value)
    fun output(key: String, value: Float) = outputTable.put(key, value)
    fun output(key: String, value: Double) = outputTable.put(key, value)
    fun output(key: String, value: ByteArray) = outputTable.put(key, value)
    fun output(key: String, value: DoubleArray) = outputTable.put(key, value)
}