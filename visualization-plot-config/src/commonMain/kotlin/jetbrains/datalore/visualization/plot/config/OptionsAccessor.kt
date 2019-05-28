package jetbrains.datalore.visualization.plot.config

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.render.point.PointShape
import jetbrains.datalore.visualization.plot.config.aes.AesOptionConversion
import kotlin.jvm.JvmOverloads

open class OptionsAccessor protected constructor(private val myOptions: Map<*, *>, defaultOptions: Map<*, *>) {
    private val myDefaultOptions: Map<*, *>

    internal val mergedOptions: Map<*, *>
        get() {
            val mergedOptions = HashMap(myDefaultOptions)
            mergedOptions.putAll(myOptions as Map<Any, Any>)
            return mergedOptions
        }

    val isEmpty: Boolean
        get() = myOptions.isEmpty() && myDefaultOptions.isEmpty()

    constructor(options: Map<*, *>) : this(options, emptyMap<Any, Any>())

    init {
        myDefaultOptions = HashMap(defaultOptions)
    }

    fun update(key: String, value: Any) {
        (myOptions as MutableMap<Any, Any>)[key] = value
    }

    protected fun update(otherOptions: Map<Any, Any>) {
        (myOptions as MutableMap<Any, Any>).putAll(otherOptions)
    }

    fun has(option: String): Boolean {
        return hasOwn(option) || myDefaultOptions[option] != null
    }

    fun hasOwn(option: String): Boolean {
        return myOptions[option] != null
    }

    operator fun get(option: String): Any? {
        return if (hasOwn(option)) {
            myOptions[option]
        } else myDefaultOptions[option]
    }

    fun getString(option: String): String? {
        return if (has(option)) {
            get(option).toString()
        } else null
    }

    fun getList(option: String): List<*> {
        val v = get(option) ?: return ArrayList<Any>()
        if (v is List<*>) {
            return v
        }
        throw IllegalArgumentException("Not a List: " + option + ": " + v::class.simpleName)
    }

    fun getDoubleList(option: String): List<Double> {
        val list = getList(option)
        val predicate: (Any?) -> Boolean = { v -> v != null && v is Double }
        if (list.all(predicate)) {
            @Suppress("UNCHECKED_CAST")
            return list as List<Double>
        }

        throw IllegalArgumentException("Expected numeric value but was : ${list.find(predicate)}")
    }

    fun getStringList(option: String): List<String> {
        val list = getList(option)
        val predicate: (Any?) -> Boolean = { v -> v != null && v is String }
        if (list.all(predicate)) {
            @Suppress("UNCHECKED_CAST")
            return list as List<String>
        }

        throw IllegalArgumentException("Expected string value but was : ${list.find(predicate)}")
    }

    internal fun getRange(option: String): ClosedRange<Double> {
        val error = { v: Any? -> throw IllegalArgumentException("'range' value is expected in form: [min, max] but was: $v") }
        val v = get(option)
        if (v is List<*> && v.isNotEmpty()) {
            val lower = asDouble(v[0]) ?: error(v)
            var upper = lower
            if (v.size > 1) {
                upper = asDouble(v[1]) ?: error(v)
            }
            return ClosedRange.closed(lower, upper)
        }

        throw IllegalArgumentException("'Range' value is expected in form: [min, max]")
    }

    fun getMap(option: String): Map<*, *> {
        val v = get(option) ?: return emptyMap<Any, Any>()
        if (v is Map<*, *>) {
            return v
        }
        throw IllegalArgumentException("Not a Map: " + option + ": " + v::class.simpleName)
    }

    @JvmOverloads
    fun getBoolean(option: String, def: Boolean = false): Boolean {
        val v = get(option)
        return v as? Boolean ?: def
    }

    fun getDouble(option: String): Double? {
        return getValueOrNull(option) { asDouble(it) }
    }

    internal fun getInteger(option: String): Int? {
        return getValueOrNull(option) { v -> (v as? Number)?.toInt() }
    }

    internal fun getLong(option: String): Long? {
        return getValueOrNull(option) { v -> (v as? Number)?.toLong() }
    }

    private fun <T> getValueOrNull(option: String, mapper: (Any?) -> T?): T? {
        val v = get(option) ?: return null
        return mapper(v)
    }

    internal fun getColor(option: String): Color? {
        return getValue(Aes.COLOR, option)
    }

    internal fun getShape(option: String): PointShape? {
        return getValue(Aes.SHAPE, option)
    }

    protected fun <T> getValue(aes: Aes<T>, option: String): T? {
        val v = get(option) ?: return null
        return AesOptionConversion.apply(aes, v)
    }

    companion object {
        internal fun over(map: Map<*, *>): OptionsAccessor {
            return OptionsAccessor(map, emptyMap<Any, Any>())
        }

        private fun asDouble(value: Any?): Double? {
            return (value as? Number)?.toDouble()
        }
    }
}
