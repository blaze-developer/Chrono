package com.blazedeveloper.chrono.structure

import android.graphics.Color
import com.blazedeveloper.chrono.structure.LogValue.Companion.asLogValue
import com.qualcomm.robotcore.hardware.NormalizedRGBA
import kotlin.math.roundToInt
import kotlin.time.Duration

class LogTable @JvmOverloads constructor(
    private val sharedTimestamp: SharedTimestamp = SharedTimestamp(Duration.ZERO),
    private val prefix: String = "/",
    private val mutableData: MutableMap<String, LogValue> = mutableMapOf(),
) {
    /** Object to hold a timestamp that can be shared between subtables. */
    class SharedTimestamp(var value: Duration)

    /** The current timestamp of the LogTable stored as a unitless Duration for easy comparisons */
    var timestamp: Duration
        @JvmName("getTimestamp") get() = sharedTimestamp.value
        @JvmName("setTimestamp") set(value) {
            sharedTimestamp.value = value
        }

    /** An immutable map of all the keys and LogValues in this table. */
    val map: Map<String, LogValue> get() = mutableData.toMap()

    /**
     * Gets a subtable with a prefix appended by a specified [name]
     * Changes to this subtable will be reflected in the parent table.
     */
    fun subtable(name: String) = LogTable(
        sharedTimestamp,
        "$prefix$name/",
        mutableData
    )

    /**
     * Creates a copy of this table exactly as it is.
     * Changes to this table will NOT be reflected in the original.
     */
    fun clone() = LogTable(
        SharedTimestamp(sharedTimestamp.value),
        prefix,
        mutableData.mapValues { (key, value) -> value.copy() }.toMutableMap()
    )

    /** Puts a raw [value] into the table at a specified [key]. */
    fun put(key: String, value: LogValue) = mutableData.put(prefix + key, value)

    /** Puts a [value] into the table at a specified [key]. */
    fun put(key: String, value: ByteArray) = put(key, value.asLogValue())

    /** Puts a [value] into the table at a specified [key]. */
    fun put(key: String, value: Boolean) = put(key, value.asLogValue())

    /** Puts a [value] into the table at a specified [key]. */
    fun put(key: String, value: Int) = put(key, value.asLogValue())

    /** Puts a [value] into the table at a specified [key]. */
    fun put(key: String, value: Long) = put(key, value.asLogValue())

    /** Puts a [value] into the table at a specified [key]. */
    fun put(key: String, value: Float) = put(key, value.asLogValue())

    /** Puts a [value] into the table at a specified [key]. */
    fun put(key: String, value: Double) = put(key, value.asLogValue())

    /** Puts a [value] into the table at a specified [key]. */
    fun put(key: String, value: String) = put(key, value.asLogValue())

    /** Puts a [value] into the table at a specified [key]. */
    fun put(key: String, value: BooleanArray) = put(key, value.asLogValue())

    /** Puts a [value] into the table at a specified [key]. */
    fun put(key: String, value: IntArray) = put(key, value.asLogValue())

    /** Puts a [value] into the table at a specified [key]. */
    fun put(key: String, value: LongArray) = put(key, value.asLogValue())

    /** Puts a [value] into the table at a specified [key]. */
    fun put(key: String, value: FloatArray) = put(key, value.asLogValue())

    /** Puts a [value] into the table at a specified [key]. */
    fun put(key: String, value: DoubleArray) = put(key, value.asLogValue())

    /** Puts a [value] into the table at a specified [key]. */
    fun put(key: String, value: Array<String>) = put(key, value.asLogValue())

    /** Puts an enum [value] represented by a string into the table at a specified [key] */
    fun <E : Enum<E>> put(key: String, value: E) = put(key, value.name)

    /** Puts an enum array [value] represented by a string into the table at a specified [key] */
    fun <E : Enum<E>> put(key: String, value: Array<E>) = put(key, value.map { it.name }.toTypedArray())

    /** Puts a NormalizedRGBA color represented by a hex triplet (WITHOUT ALPHA) into the table at a specified key */
    fun put(key: String, value: NormalizedRGBA) = put(key, value.toHexTriplet())

    /**
     * Gets a raw LogValue from the table at the specified [key].
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    fun get(key: String, default: LogValue): LogValue {
        val logValue = mutableData[prefix + key] ?: default
        return if (logValue.type == default.type) logValue else default
    }

    /**
     * Gets an item from the table at the specified [key].
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    fun get(key: String, default: ByteArray) =
        get(key, default.asLogValue()).value as ByteArray

    /**
     * Gets an item from the table at the specified [key].
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    fun get(key: String, default: Boolean) =
        get(key, default.asLogValue()).value as Boolean

    /**
     * Gets an item from the table at the specified [key].
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    fun get(key: String, default: Int) =
        get(key, default.asLogValue()).value as Int

    /**
     * Gets an item from the table at the specified [key].
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    fun get(key: String, default: Long) =
        get(key, default.asLogValue()).value as Long

    /**
     * Gets an item from the table at the specified [key].
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    fun get(key: String, default: Float) =
        get(key, default.asLogValue()).value as Float

    /**
     * Gets an item from the table at the specified [key].
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    fun get(key: String, default: Double) =
        get(key, default.asLogValue()).value as Double

    /**
     * Gets an item from the table at the specified [key].
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    fun get(key: String, default: String) =
        get(key, default.asLogValue()).value as String

    /**
     * Gets an item from the table at the specified [key].
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    fun get(key: String, default: BooleanArray) =
        get(key, default.asLogValue()).value as BooleanArray

    /**
     * Gets an item from the table at the specified [key].
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    fun get(key: String, default: IntArray) =
        get(key, default.asLogValue()).value as IntArray

    /**
     * Gets an item from the table at the specified [key].
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    fun get(key: String, default: LongArray) =
        get(key, default.asLogValue()).value as LongArray

    /**
     * Gets an item from the table at the specified [key].
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    fun get(key: String, default: FloatArray) =
        get(key, default.asLogValue()).value as FloatArray

    /**
     * Gets an item from the table at the specified [key].
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    fun get(key: String, default: DoubleArray) =
        get(key, default.asLogValue()).value as DoubleArray

    /**
     * Gets an item from the table at the specified [key].
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    @Suppress("UNCHECKED_CAST")
    fun get(key: String, default: Array<String>) =
        get(key, default.asLogValue()).value as Array<String>

    /** Gets an enum item from the table at the specified [key],
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    inline fun <reified E : Enum<E>> get(key: String, default: E) =
        enumValueOf<E>(get(key, default.name))

    /**
     * Gets an enum array item from the table at the specified [key],
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    inline fun <reified E: Enum<E>> get(key: String, default: Array<E>) =
        get(key, default.map { it.name }.toTypedArray()).map { enumValueOf<E>(it) }.toTypedArray()

    /**
     * Gets a color object from the table at the specified [key],
     * If the data does not exist or is of the wrong type,
     * the [default] is returned.
     */
    fun get(key: String, default: NormalizedRGBA): NormalizedRGBA =
        NormalizedRGBA().apply {
            val color = Color.parseColor(get(key, default.toHexTriplet()))
            alpha = 1f
            red = Color.red(color) / 255f
            green = Color.green(color) / 255f
            blue = Color.blue(color) / 255f
        }

    fun NormalizedRGBA.toHexTriplet(): String {
        val r = (red   * 255f).roundToInt().coerceIn(0, 255)
        val g = (green * 255f).roundToInt().coerceIn(0, 255)
        val b = (blue  * 255f).roundToInt().coerceIn(0, 255)

        return "#%02X%02X%02X".format(r, g, b)
    }
}