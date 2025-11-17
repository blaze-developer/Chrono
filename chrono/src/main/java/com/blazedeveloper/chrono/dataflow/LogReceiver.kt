package com.blazedeveloper.chrono.dataflow

import com.blazedeveloper.chrono.structure.LogTable

interface LogReceiver {
    fun start() {}
    fun stop() {}
    @Throws(InterruptedException::class)
    fun receive(table: LogTable)
}