/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.render.point.PointShape
import jetbrains.datalore.plot.config.aes.AesOptionConversion
import kotlin.jvm.JvmOverloads

open class OptionsAccessor(
    private val options: Map<String, Any>,
    private val defaultOptions: Map<String, Any> = emptyMap<String, Any>()
) {
    val mergedOptions: Map<String, Any>
        get() = defaultOptions + options

    val isEmpty: Boolean
        get() = options.isEmpty() && defaultOptions.isEmpty()

    fun update(key: String, value: Any) {
        @Suppress("UNCHECKED_CAST")
        (options as MutableMap<String, Any>)[key] = value
    }

    protected fun update(otherOptions: Map<String, Any>) {
        (options as MutableMap<String, Any>).putAll(otherOptions)
    }

    fun has(option: String): Boolean {
        return hasOwn(option) || defaultOptions[option] != null
    }

    fun hasOwn(option: String): Boolean {
        return options[option] != null
    }

    operator fun get(option: String): Any? {
        return if (hasOwn(option)) {
            options[option]
        } else {
            defaultOptions[option]
        }
    }

    fun getSafe(option: String): Any {
        return get(option) ?: throw IllegalStateException("Option `$option` not found.")
    }

    fun getString(option: String): String? {
        return get(option)?.toString()
    }

    fun getStringSafe(option: String): String {
        return getString(option)
            ?: throw IllegalArgumentException("Can't get string value: option '$option' is not present.")
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

    fun getOrderedBoundedDoubleDistinctPair(
        option: String,
        lowerBound: Double,
        upperBound: Double
    ): Pair<Double, Double> {
        val pair = pickTwo(option, getBoundedDoubleList(option, lowerBound, upperBound))
        check(pair.first < pair.second) { "Value ${pair.first} should be lower than ${pair.second}" }
        return pair
    }

    fun getBoundedDoubleList(option: String, lowerBound: Double, upperBound: Double): List<Double> {
        val list = getDoubleList(option)
        list.forEach {
            check(it in lowerBound..upperBound) { "Value $it is not in range [$lowerBound, $upperBound]" }
        }
        return list
    }

    fun getNumPair(option: String): Pair<Number, Number> {
        val list = getNumList(option) { it is Number }
        @Suppress("UNCHECKED_CAST")
        return pickTwo(option, list) as Pair<Number, Number>
    }

    fun getNumQPair(option: String): Pair<Number?, Number?> {
        val list = getNumList(option) { it == null || it is Number }
        return pickTwo(option, list)
    }

    fun getNumPairDef(option: String, def: Pair<Number, Number>): Pair<Number, Number> {
        return if (has(option)) {
            getNumPair(option)
        } else {
            def
        }
    }

    fun getNumQPairDef(option: String, def: Pair<Number?, Number?>): Pair<Number?, Number?> {
        return if (has(option)) {
            getNumQPair(option)
        } else {
            def
        }
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

    fun getAsList(option: String): List<Any?> {
        val v = get(option) ?: emptyList<Any>()
        return if (v is List<*>) {
            v
        } else {
            listOf(v)
        }
    }

    fun getAsStringList(option: String): List<String> {
//        val v = get(option) ?: emptyList<String>()
//        return if (v is List<*>) {
//            v.filterNotNull().map { it.toString() }
//        } else {
//            listOf(v.toString())
//        }
        return getAsList(option).filterNotNull().map { it.toString() }
    }

    fun getStringList(option: String): List<String> {
        val list = getList(option)

        requireAll(list, { it is String }) { "$option requires a list of strings but not string encountered: $it" }

        @Suppress("UNCHECKED_CAST")
        return list as List<String>
    }

    internal fun getRange(option: String): DoubleSpan {
        require(has(option)) { "'Range' value is expected in form: [min, max]" }

        val range = getRangeOrNull(option)

        requireNotNull(range) { "'range' value is expected in form: [min, max] but was: ${get(option)}" }

        return range
    }

    fun getRangeOrNull(option: String): DoubleSpan? {
        val pair = get(option)
        if ((pair is List<*> && pair.size == 2 && pair.all { it is Number }) != true) {
            return null
        }

        val lower = (pair.first() as Number).toDouble()
        val upper = (pair.last() as Number).toDouble()

        return try {
            DoubleSpan(lower, upper)
        } catch (ex: Throwable) {
            null
        }
    }

    fun getMap(option: String): Map<String, Any> {
        val v = get(option) ?: return emptyMap<String, Any>()
        require(v is Map<*, *>) { "Not a Map: " + option + ": " + v::class.simpleName }

        @Suppress("UNCHECKED_CAST")
        return v as Map<String, Any>
    }

    @JvmOverloads
    fun getBoolean(option: String, def: Boolean = false): Boolean {
        val v = get(option)
        return v as? Boolean ?: def
    }

    fun getDouble(option: String): Double? {
        return getNumber(option)?.toDouble()
    }

    fun getDoubleSafe(option: String): Double {
        return getNumber(option)?.toDouble()
            ?: throw IllegalArgumentException("Can't get double value: option '$option' is not present.")
    }

    fun getInteger(option: String): Int? {
        return getNumber(option)?.toInt()
    }

    fun getLong(option: String): Long? {
        return getNumber(option)?.toLong()
    }

    fun getDoubleDef(option: String, def: Double): Double {
        return getDouble(option) ?: def
    }

    fun getIntegerDef(option: String, def: Int): Int {
        return getInteger(option) ?: def
    }

    fun getLongDef(option: String, def: Long): Long {
        return getLong(option) ?: def
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
        fun over(map: Map<String, Any>): OptionsAccessor {
            return OptionsAccessor(map)
        }

        private fun <T> requireAll(items: Iterable<T>, predicate: (T) -> Boolean, lazy: (T) -> Any) {
            items.filterNot { predicate(it) }.firstOrNull()?.let { require(false) { lazy(it) } }
        }
    }
}
