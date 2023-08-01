/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.conversion

import org.jetbrains.letsPlot.commons.intern.function.Function
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes

object AesOptionConversion {
    private val CONVERTERS_MAP = TypedOptionConverterMap()

    fun <T> getConverter(aes: Aes<T>): (Any?) -> T? {
        return CONVERTERS_MAP[aes]
    }

    fun <T> apply(aes: Aes<T>, optionValue: Any): T? {
        val converter = getConverter(aes)
        return converter(optionValue)
    }

    fun <T> applyToList(aes: Aes<T>, optionValues: List<*>): List<T?> {
        val converter = getConverter(aes)
        val result = ArrayList<T?>()
        for (optionValue in optionValues) {
            result.add(converter(optionValue!!))
        }
        return result
    }

    /**
     * For tests
     */
    fun has(aes: Aes<*>): Boolean {
        return CONVERTERS_MAP.containsKey(aes)
    }

    fun updateWith(converter: Function<Any?, Color?>) {
        val cnv = { o: Any? -> converter.apply(o) }
        CONVERTERS_MAP.put(Aes.COLOR, cnv)
        CONVERTERS_MAP.put(Aes.FILL, cnv)
        CONVERTERS_MAP.put(Aes.PAINT_A, cnv)
        CONVERTERS_MAP.put(Aes.PAINT_B, cnv)
        CONVERTERS_MAP.put(Aes.PAINT_C, cnv)
    }
}
