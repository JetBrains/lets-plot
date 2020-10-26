/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.render.point.PointShape
import jetbrains.datalore.plot.config.aes.AesOptionConversion
import kotlin.jvm.JvmOverloads

open class OptionsAccessor protected constructor(private val myOptions: Map<*, *>, defaultOptions: Map<*, *>) {
    private val myDefaultOptions: Map<*, *>

    val mergedOptions: Map<*, *>
        get() {
            val mergedOptions = HashMap(myDefaultOptions)
            mergedOptions.putAll(myOptions)
            return mergedOptions
        }

    val isEmpty: Boolean
        get() = myOptions.isEmpty() && myDefaultOptions.isEmpty()

    constructor(options: Map<*, *>) : this(options, emptyMap<Any, Any>())

    init {
        myDefaultOptions = HashMap(defaultOptions)
    }

    fun update(key: String, value: Any) {
        @Suppress("UNCHECKED_CAST")
        (myOptions as MutableMap<String, Any>)[key] = value
    }

    protected fun update(otherOptions: Map<Any, Any>) {
        @Suppress("UNCHECKED_CAST")
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

        require(v is List<*>) { "Not a List: $option: ${v::class.simpleName}" }

        return v
    }

    fun getDoubleList(option: String): List<Double> {
        val list = getNumList(option)
        return list.map { it.toDouble() }
    }

    @Suppress("UNCHECKED_CAST")
    fun getNumPair(option: String): Pair<Number, Number> {
        val list = getNumList(option) { it is Number }
        return pickTwo(option, list) as Pair<Number, Number>
    }

    fun getNumQPair(option: String): Pair<Number?, Number?> {
        val list = getNumList(option) { it == null || it is Number }
        require(list.size >= 2) { "$option requires a list of 2 but was ${list.size}" }
        return Pair(list[0], list[1])
    }

    private fun <T> pickTwo(option: String, list: List<T>): Pair<T, T> {
        require(list.size >= 2) { "$option requires a list of 2 but was ${list.size}" }
        return Pair(list[0], list[1])
    }

    @Suppress("UNCHECKED_CAST")
    fun getNumList(option: String): List<Number> = getNumList(option) { it is Number } as List<Number>

    fun getNumQList(option: String): List<Number?> = getNumList(option) { it == null || it is Number }

    private fun getNumber(option: String): Number? {
        val v = get(option) ?: return null

        require(v is Number) { "Parameter '$option' expected to be a Number, but was ${v::class.simpleName}" }

        return v;
    }

    private fun getNumList(option: String, predicate: (Any?) -> Boolean): List<Number?> {
        val list = getList(option)

        requireAll(list, predicate) { "$option requires a list of numbers but not numeric encountered: $it" }

        @Suppress("UNCHECKED_CAST")
        return list as List<Number?>
    }

    fun getStringList(option: String): List<String> {
        val list = getList(option)

        requireAll(list, { it is String }) { "$option requires a list of strings but not string encountered: $it" }

        @Suppress("UNCHECKED_CAST")
        return list as List<String>
    }

    internal fun getRange(option: String): ClosedRange<Double> {
        require(has(option)) { "'Range' value is expected in form: [min, max]" }

        val range = getRangeOrNull(option)

        requireNotNull(range) { "'range' value is expected in form: [min, max] but was: ${get(option)}" }

        return range
    }

    fun getRangeOrNull(option: String): ClosedRange<Double>? {
        val pair = get(option)
        if ((pair is List<*> && pair.size == 2 && pair.all { it is Number }) != true) {
            return null
        }

        val lower = (pair.first() as Number).toDouble()
        val upper = (pair.last() as Number).toDouble()

        return try {
            ClosedRange(lower, upper)
        } catch (ex: Throwable) {
            null
        }
    }

    fun getMap(option: String): Map<*, *> {
        val v = get(option) ?: return emptyMap<Any, Any>()

        require(v is Map<*, *>) { "Not a Map: " + option + ": " + v::class.simpleName }

        return v
    }

    @JvmOverloads
    fun getBoolean(option: String, def: Boolean = false): Boolean {
        val v = get(option)
        return v as? Boolean ?: def
    }

    fun getDouble(option: String): Double? {
        return getNumber(option)?.toDouble()
    }

    fun getInteger(option: String): Int? {
        return getNumber(option)?.toInt()
    }

    fun getLong(option: String): Long? {
        return getNumber(option)?.toLong()
    }

    private fun <T> getValueOrNull(option: String, mapper: (Any?) -> T?): T? {
        val v = get(option) ?: return null
        return mapper(v)
    }

    fun getColor(option: String): Color? {
        return getValue(Aes.COLOR, option)
    }

    fun getShape(option: String): PointShape? {
        return getValue(Aes.SHAPE, option)
    }

    protected fun <T> getValue(aes: Aes<T>, option: String): T? {
        val v = get(option) ?: return null
        return AesOptionConversion.apply(aes, v)
    }

    companion object {
        fun over(map: Map<*, *>): OptionsAccessor {
            return OptionsAccessor(map, emptyMap<Any, Any>())
        }

        private fun <T> requireAll(items: Iterable<T>, predicate: (T) -> Boolean, lazy: (T) -> Any) {
            items.filterNot { predicate(it) }.firstOrNull()?.let { require(false) { lazy(it) } }
        }
    }
}
