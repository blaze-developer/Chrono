package com.blazedeveloper.chrono.input

import com.blazedeveloper.chrono.Logger
import kotlin.time.Duration
import kotlin.time.DurationUnit

/** A timer for timing robot functions that uses synchronized and deterministic timestamps. */
class LoggedTimer(
    private var startTime: Duration = Logger.timestamp
) {
    /** Resets the timer to 0 right now. */
    fun reset() { startTime = Logger.timestamp }

    /**
     * The time elapsed as a unitless Duration
     * that can be converted to and from any unit.
     */
    @get:JvmSynthetic
    val elapsed get() = Logger.timestamp - startTime

    /** The time elapsed in seconds */
    @get:JvmName("seconds")
    val seconds get() = elapsed.toDouble(DurationUnit.SECONDS)

    /** The time elapsed in milliseconds.  */
    @get:JvmName("milliseconds")
    val milliseconds get() = elapsed.toDouble(DurationUnit.SECONDS)

    /** The time elapsed in nanoseconds. */
    @get:JvmName("nanoseconds")
    val nanoseconds get() = elapsed.inWholeNanoseconds

    /** Logs the current timed duration of the timer to the console in seconds. */
    fun log() = println("[LoggedTimer] ELAPSED: $this")

    /** Gets a string representation of the time elapsed in seconds. */
    override fun toString() = String.format("$.4f seconds", seconds)
}