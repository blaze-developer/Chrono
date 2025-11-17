package com.blazedeveloper.chrono.structure

data class LogValue(val value: Any, val type: LoggableType) {
    companion object {
        fun ByteArray.asLogValue() = LogValue(this, LoggableType.ByteArray)
        fun Boolean.asLogValue() = LogValue(this, LoggableType.Boolean)
        fun Int.asLogValue() = LogValue(this, LoggableType.Integer)
        fun Long.asLogValue() = LogValue(this, LoggableType.Integer)
        fun Float.asLogValue() = LogValue(this, LoggableType.Float)
        fun Double.asLogValue() = LogValue(this, LoggableType.Double)
        fun String.asLogValue() = LogValue(this, LoggableType.String)
        fun BooleanArray.asLogValue() = LogValue(this, LoggableType.BooleanArray)
        fun IntArray.asLogValue() = LogValue(this, LoggableType.IntegerArray)
        fun LongArray.asLogValue() = LogValue(this, LoggableType.IntegerArray)
        fun FloatArray.asLogValue() = LogValue(this, LoggableType.FloatArray)
        fun DoubleArray.asLogValue() = LogValue(this, LoggableType.DoubleArray)
        fun Array<String>.asLogValue() = LogValue(this, LoggableType.StringArray)
    }
}

enum class LoggableType(val wpilogType: String) {
    ByteArray("raw"),
    Boolean("boolean"),
    Integer("int64"),
    Float("float"),
    Double("double"),
    String("string"),
    BooleanArray("boolean[]"),
    IntegerArray("int64[]"),
    FloatArray("float[]"),
    DoubleArray("double[]"),
    StringArray("string[]")
}