package com.blazedeveloper.chrono

import com.blazedeveloper.chrono.input.replayFromTable
import com.blazedeveloper.chrono.input.writeToTable
import com.blazedeveloper.chrono.structure.LogTable
import com.blazedeveloper.chrono.structure.LoggableInputs
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

abstract class LoggedLinearOpMode : LinearOpMode() {
    /** Provides a deterministic and replayable replacement for [opModeIsActive] */
    protected val isActive get() = opmodeInputs.isActive

    /** Provides a deterministic and replayable replacement for [opModeInInit] */
    protected val inInit get() = opmodeInputs.inInit

    /** Provides a deterministic and replayable replacement for [isStopRequested] */
    protected val shouldStop get() = opmodeInputs.stopRequested

    /** Whether or not we are in the first log cycle. */
    private var isFirstCycle = true

    /** Override this method and place your code here. */
    abstract fun runLoggedOpMode()

    /** Handles logger lifecycle methods before calling the user code. */
    final override fun runOpMode() {
        Logger.start()

        Logger.preUser() // Start the very first log cycle, or replay the first table.
        updateOpModeInputs() // Update initial opmode inputs to reflect reality or a replay

        /**
         * Run the user code.
         * The first log cycle is ended is ended upon the first user log cycle.
         */
        runLoggedOpMode()

        Logger.stop()
    }

    /** Waits until the OpMode is active using deterministic data. */
    override fun waitForStart() { while (!isActive) { /** Do nothing */ } }

    /**
     * This NEEDS TO BE USED to wrap robot code iterations, e.g. loops on [isActive] or [inInit].
     * Using this to wrap your cycles automatically runs logger lifecycle methods and
     * ensures that data is logged and replayed properly.
     */
    fun loggedCycle(userCode: () -> Unit) {
        // If this is the very first robot cycle, end the init cycle and log its data.
        if (isFirstCycle) {
            Logger.postUser()
            isFirstCycle = false
        }

        Logger.preUser()
        updateOpModeInputs()
        userCode()
        Logger.postUser()
    }

    /**
     * Properly updates, and processes deterministic lifecycle inputs:
     * [inInit], [isActive], [shouldStop], and gamepad input.
     * Ensure you call this, and Logger lifecycle methods in robot iterations if not using
     * convenience method [loggedCycle] that call this automatically.
     */
    protected fun updateOpModeInputs() {
        if (!Logger.hasReplaySource) {
            opmodeInputs.inInit = opModeInInit()
            opmodeInputs.isActive = opModeIsActive()
            opmodeInputs.stopRequested = isStopRequested
        }
        Logger.processInputs("LoggedOpMode", opmodeInputs)
    }

    private val opmodeInputs = object : LoggableInputs {
        // These will be initialized during the first log cycle.
        var isActive = false
        var inInit = false
        var stopRequested = false

        override fun toLog(table: LogTable) {
            table.put("isActive", isActive)
            table.put("inInit", inInit)
            table.put("stopRequested", stopRequested)

            val gamepads = table.subtable("Gamepads")
            gamepad1.writeToTable(gamepads, 1)
            gamepad2.writeToTable(gamepads, 2)
        }

        override fun fromLog(table: LogTable) {
            isActive = table.get("isActive", isActive)
            inInit = table.get("inInit", inInit)
            stopRequested = table.get("stopRequested", stopRequested)

            val gamepads = table.subtable("Gamepads")
            gamepad1.replayFromTable(gamepads, 1)
            gamepad2.replayFromTable(gamepads, 2)
        }
    }
}