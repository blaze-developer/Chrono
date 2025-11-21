package com.blazedeveloper.chrono

import com.blazedeveloper.chrono.dataflow.LogReceiver
import com.blazedeveloper.chrono.dataflow.ReplaySource
import com.blazedeveloper.chrono.output.ConsoleLogger
import com.blazedeveloper.chrono.structure.LogTable
import com.blazedeveloper.chrono.structure.LoggableInputs
import kotlin.system.exitProcess
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource.Monotonic
import kotlin.time.measureTime

object Logger {
    private lateinit var table: LogTable
    private lateinit var outputsTable: LogTable
    private lateinit var timingsTable: LogTable

    private val logReceivers = mutableListOf<LogReceiver>()
    private val metadataPairs = mutableListOf<Pair<String, String>>()
    var replaySource: ReplaySource? = null
    val hasReplaySource: Boolean get() = replaySource != null

    private lateinit var loggerStart: TimeMark
    private lateinit var cycleStart: TimeMark
    private lateinit var timeBeforeUser: TimeMark

    internal var enabledKey: String? = null
    internal var autoKey: String? = null
    internal var joystickKey: String? = null

    private var running = false
    private fun ifRunning(block: () -> Unit) { if(running) block() }
    private fun ifStopped(block: () -> Unit) { if(!running) block() }

    fun interface Addable<T> {
        fun add(toAdd: T)
        operator fun plusAssign(toAdd: T) = add(toAdd)
    }

    /**
     * The synchronized timestamp of the current cycle,
     * this should be used for all replayed logic as it is deterministic and replayable.
     * If the logger is not running, returns 0.
     **/
    @get:JvmName("timestamp")
    @get:JvmStatic
    val timestamp: Duration get() = if (running) table.timestamp else Duration.ZERO

    /** Adds metadata to be logged to the table when the Logger is started. */
    @JvmStatic
    fun addMetadata(key: String, value: String) = ifStopped { metadataPairs += key to value }

    /** Adds a receiver to the Logger */
    @JvmStatic
    fun addReceiver(receiver: LogReceiver) = ifStopped { logReceivers += receiver }

    /**
     * Object that user can add log receivers to that accept log
     * data from the Logger and use for streaming, logfiles, etc.
     */
    val receivers = Addable<LogReceiver> { addReceiver(it) }

    /**
     * Maps log metadata names to values to be put into the table when
     * the Logger is started.
     */
    val metadata = Addable<Pair<String, String>> { addMetadata(it.first, it.second) }

    /** Starts the Logger, its receivers, and sources. */
    internal fun start() = ifStopped {
        running = true

        ConsoleLogger.start()

        // Initialize values for this run.
        table = LogTable()
        outputsTable = table.subtable(if (!hasReplaySource) "RealOutputs" else "ReplayOutputs")
        timingsTable = outputsTable.subtable("LoggerTimings")
        loggerStart = Monotonic.markNow()

        // Log metadata
        table.subtable(
            if (!hasReplaySource) "RealMetadata"
            else "ReplayMetadata"
        ).apply {
            metadataPairs.forEach { (key, value) -> put(key, value) }
        }

        // Log metadata format
        table.subtable("LogMetadata").apply {
            mapOf(
                "EnabledKey" to enabledKey,
                "AutoKey" to autoKey,
                "JoystickKey" to joystickKey
            ).forEach { (key, value) -> value?.let { put(key, it) } }
        }

        logReceivers.forEach { it.start() }

        replaySource?.start()
    }

    /** Sets up the table for this cycle. Runs before user code. **/
    internal fun preUser() = ifRunning {
        cycleStart = Monotonic.markNow()

        if (hasReplaySource) {
            // Update table from the replay source, end if the source ends.
            val tableReadTime = measureTime {
                val updated = replaySource?.updateTable(table) ?: false
                if (!updated) {
                    exitProcess(0)
                }
            }
            timingsTable.put("TableReadNS", tableReadTime.inWholeNanoseconds)
        } else {
            table.timestamp = loggerStart.elapsedNow()
        }

        timeBeforeUser = Monotonic.markNow()
    }

    /** Processes an input for this loop, either logging or replaying from the table. **/
    @JvmStatic
    fun processInputs(subtableName: String, inputs: LoggableInputs) = ifRunning {
        if(hasReplaySource) {
            inputs.fromLog(table.subtable(subtableName))
        } else {
            inputs.toLog(table.subtable(subtableName))
        }
    }

    // Output methods for user code to publish output data.
    @JvmStatic fun output(key: String, value: String) = ifRunning { outputsTable.put(key, value) }
    @JvmStatic fun output(key: String, value: Boolean) = ifRunning { outputsTable.put(key, value) }
    @JvmStatic fun output(key: String, value: Int) = ifRunning { outputsTable.put(key, value) }
    @JvmStatic fun output(key: String, value: Long) = ifRunning { outputsTable.put(key, value) }
    @JvmStatic fun output(key: String, value: Float) = ifRunning { outputsTable.put(key, value) }
    @JvmStatic fun output(key: String, value: Double) = ifRunning { outputsTable.put(key, value) }
    @JvmStatic fun output(key: String, value: ByteArray) = ifRunning { outputsTable.put(key, value) }
    @JvmStatic fun output(key: String, value: DoubleArray) = ifRunning { outputsTable.put(key, value) }

    /** Sends data to receivers. Runs after user code. **/
    internal fun postUser() = ifRunning {
        ConsoleLogger.log()

        val userCodeTime = timeBeforeUser.elapsedNow()
        timingsTable.put("UserCodeNS", userCodeTime.inWholeNanoseconds)

        // Record Timings
        val fullCycleTime = cycleStart.elapsedNow()
        val loggerCycleTime = fullCycleTime - userCodeTime
        timingsTable.put("FullCycleNS", fullCycleTime.inWholeNanoseconds)
        timingsTable.put("LoggerCycleNS", loggerCycleTime.inWholeNanoseconds)

        val tableToReceive = table.clone()
        logReceivers.forEach { it.receive(tableToReceive) }
    }

    /** Stops the Logger, its receivers, and sources.*/
    internal fun stop() = ifRunning {
        running = false

        logReceivers.forEach { it.stop() }
        replaySource?.stop()

        // Reset the logger for the next run.
        reset()

        ConsoleLogger.stop()
    }

    private fun reset() {
        logReceivers.clear()
        metadataPairs.clear()
        replaySource = null
        enabledKey = null
        autoKey = null
        joystickKey = null
    }
}