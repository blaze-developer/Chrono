package com.blazedeveloper.chrono.structure

import com.qualcomm.robotcore.hardware.NormalizedRGBA
import kotlin.reflect.KProperty

interface LoggableInputs {
    fun toLog(table: LogTable)
    fun fromLog(table: LogTable)
}

abstract class AutoLoggableInputs : LoggableInputs {
    inner class Field<T>(
        val key: String,
        var value: T,
        val toLog: LogTable.(String, T) -> Unit,
        val fromLog: LogTable.(String, T) -> T
    ) {
        operator fun setValue(thisRef: Any, property: KProperty<*>, newValue: T) { value = newValue }
        operator fun getValue(thisRef: Any, property: KProperty<*>) = value

        operator fun provideDelegate(thisRef: Any, property: KProperty<*>): Field<T> {
            toLogs.add { it.toLog(key, value) }
            fromLogs.add { it.fromLog(key, value) }
            return this
        }
    }

    fun logged(key: String, value: LogValue) = Field(key, value, LogTable::put, LogTable::get)
    fun logged(key: String, value: ByteArray) = Field(key, value, LogTable::put, LogTable::get)
    fun logged(key: String, value: Boolean) = Field(key, value, LogTable::put, LogTable::get)
    fun logged(key: String, value: Int) = Field(key, value, LogTable::put, LogTable::get)
    fun logged(key: String, value: Long) = Field(key, value, LogTable::put, LogTable::get)
    fun logged(key: String, value: Float) = Field(key, value, LogTable::put, LogTable::get)
    fun logged(key: String, value: Double) = Field(key, value, LogTable::put, LogTable::get)
    fun logged(key: String, value: String) = Field(key, value, LogTable::put, LogTable::get)
    fun logged(key: String, value: BooleanArray) = Field(key, value, LogTable::put, LogTable::get)
    fun logged(key: String, value: IntArray) = Field(key, value, LogTable::put, LogTable::get)
    fun logged(key: String, value: LongArray) = Field(key, value, LogTable::put, LogTable::get)
    fun logged(key: String, value: FloatArray) = Field(key, value, LogTable::put, LogTable::get)
    fun logged(key: String, value: DoubleArray) = Field(key, value, LogTable::put, LogTable::get)
    fun logged(key: String, value: Array<String>) = Field(key, value, LogTable::put, LogTable::get)
    inline fun <reified E: Enum<E>> logged(key: String, value: E) = Field(key, value, LogTable::put, LogTable::get)
    inline fun <reified E: Enum<E>> logged(key: String, value: Array<E>) = Field(key, value, LogTable::put, LogTable::get)
    fun logged(key: String, value: NormalizedRGBA) = Field(key, value, LogTable::put, LogTable::get)

    val toLogs = mutableListOf<(LogTable) -> Unit>()
    val fromLogs = mutableListOf<(LogTable) -> Unit>()

    final override fun toLog(table: LogTable) = toLogs.forEach { it(table) }
    final override fun fromLog(table: LogTable) = fromLogs.forEach { it(table) }
}