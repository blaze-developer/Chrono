package com.blazedeveloper.chrono.structure

interface LoggableInputs {
    fun toLog(table: LogTable)
    fun fromLog(table: LogTable)
}