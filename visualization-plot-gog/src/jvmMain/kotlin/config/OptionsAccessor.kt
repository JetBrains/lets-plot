package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.config.aes.AesOptionConversion
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.point.PointShape

open class OptionsAccessor protected constructor(private val myOptions: Map<*, *>, defaultOptions: Map<*, *>) {
    private val myDefaultOptions: Map<*, *>

    internal val mergedOptions: Map<*, *>
        get() {
            val mergedOptions = HashMap<Any, Any>(myDefaultOptions)
            mergedOptions.putAll(myOptions as Map<Any, Any>)
            return mergedOptions
        }

    val isEmpty: Boolean
        get() = myOptions.isEmpty() && myDefaultOptions.isEmpty()

    constructor(options: Map<*, *>) : this(options, emptyMap<Any, Any>()) {}

    init {
        myDefaultOptions = HashMap(defaultOptions)
    }

    fun update(key: String, value: Any) {
        (myOptions as MutableMap<Any, Any>).put(key, value)
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
        throw IllegalArgumentException("Not a List: " + option + ": " + v.javaClass.simpleName)
    }

    internal fun getRange(option: String): ClosedRange<Double> {
        val v = get(option)
        if (v is List<*> && !v.isEmpty()) {
            val list = v as List<*>
            val lower = asDouble(list[0]!!)
            var upper = lower
            if (list.size > 1) {
                upper = asDouble(list[1]!!)
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
        throw IllegalArgumentException("Not a Map: " + option + ": " + v.javaClass.simpleName)
    }

    @JvmOverloads
    fun getBoolean(option: String, def: Boolean = false): Boolean {
        val v = get(option)
        return if (v is Boolean) {
            (v as Boolean?)!!
        } else def
    }

    fun getDouble(option: String): Double? {
        return getValueOrNull(option, { it: Any -> asDouble(it) })
    }

    internal fun getInteger(option: String): Int? {
        return getValueOrNull<Int>(option, { v: Any -> (v as Number).toInt() })
    }

    internal fun getLong(option: String): Long? {
        return getValueOrNull<Long>(option, { v: Any -> (v as Number).toLong() })
    }

    private fun <T> getValueOrNull(option: String, mapper: (Any) -> T): T? {
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

        private fun asDouble(value: Any): Double {
            return (value as Number).toDouble()
        }
    }
}
