package com.blazedeveloper.chrono.inputs

import com.blazedeveloper.chrono.structure.LogTable
import com.qualcomm.robotcore.hardware.Gamepad

fun Gamepad.writeToTable(table: LogTable, index: Int) {
    table.put("Gamepad$index", toByteArray())
}

fun Gamepad.replayFromTable(table: LogTable, index: Int) {
    val bytes = table.get("Gamepad$index", toByteArray())
    fromByteArray(bytes)
}