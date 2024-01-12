/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShape
import org.jetbrains.letsPlot.core.spec.conversion.AesOptionConversion
import kotlin.jvm.JvmOverloads

open class OptionsAccessor(
    private val options: Map<String, Any>,
    private val defaultOptions: Map<String, Any> = emptyMap<String, Any>(),
) {
    fun update(key: String, value: Any) {
        (options as MutableMap<String, Any>)[key] = value
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

    fun getNumPair(option: String): Pair<Number, Number> {
        val list = getNumList(option)
        return pickTwo(option, list)
    }

    fun getNumQPair(option: String): Pair<Number?, Number?> {
        val list = getNumQList(option)
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

    private fun getNumber(option: String): Number? {
        val v = get(option) ?: return null
        require(v is Number) { "Parameter '$option' expected to be a Number, but was ${v::class.simpleName}" }
        return v;
    }

    fun getList(option: String): List<*> {
        val v = get(option) ?: emptyList<Any>()
        require(v is List<*>) { "Not a List: $option: ${v::class.simpleName}" }
        return v
    }

    private fun getNumQList(option: String): List<Number?> {
        val list = getList(option)
        requireAll(list, { it == null || it is Number }) { o, index ->
            "The option '$option' requires a list of numbers but element [$index] is: $o"
        }
        @Suppress("UNCHECKED_CAST")
        return list as List<Number?>
    }

    private fun getNumList(option: String): List<Number> {
        val list = getNumQList(option)
        requireAll(list, { it is Number }) { o, index ->
            "The option '$option' requires a list of numbers but element [$index] is: $o"
        }
        @Suppress("UNCHECKED_CAST")
        return list as List<Number>
    }

    fun getDoubleList(option: String): List<Double> {
        val list = getNumList(option)
        return list.map { it.toDouble() }
    }

    fun getStringList(option: String): List<String> {
        val list = getList(option)
        requireAll(list, { it is String }) { o, index ->
            "The option '$option' requires a list of strings but element [$index] is: $o"
        }
        @Suppress("UNCHECKED_CAST")
        return list as List<String>
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
        return getAsList(option).filterNotNull().map { it.toString() }
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

    fun getIntegerSafe(option: String): Int {
        return getNumber(option)?.toInt()
            ?: throw IllegalArgumentException("Can't get integer value: option '$option' is not present.")
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

    fun getColor(option: String, aopConversion: AesOptionConversion): Color? {
        return getValue(Aes.COLOR, option, aopConversion)
    }

    fun getShape(option: String, aopConversion: AesOptionConversion): PointShape? {
        return getValue(Aes.SHAPE, option, aopConversion)
    }

    protected fun <T> getValue(aes: Aes<T>, option: String, aopConversion: AesOptionConversion): T? {
        val v = get(option) ?: return null
        return aopConversion.apply(aes, v)
    }

    fun toMap(): Map<String, Any> {
        return defaultOptions + options
    }

    companion object {
        fun over(map: Map<String, Any>): OptionsAccessor {
            return OptionsAccessor(map)
        }

        private fun requireAll(
            items: Iterable<*>,
            predicate: (Any?) -> Boolean,
            message: (Any?, Int) -> String
        ) {
            require(items.all(predicate)) {
                val el = items.find { !(predicate(it)) }
                val i = items.indexOf(el)
                message(el, i)
            }
        }
    }
}
