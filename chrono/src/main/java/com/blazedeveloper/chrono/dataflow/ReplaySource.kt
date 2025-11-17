package com.blazedeveloper.chrono.dataflow

import com.blazedeveloper.chrono.structure.LogTable

interface ReplaySource {
    fun start()
    fun stop()
    fun updateTable(table: LogTable): Boolean
    operator fun invoke(table: LogTable) = updateTable(table)
}