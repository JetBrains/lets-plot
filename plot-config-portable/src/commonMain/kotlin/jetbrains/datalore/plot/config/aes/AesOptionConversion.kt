/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.aes

import org.jetbrains.letsPlot.core.plot.base.Aes

object AesOptionConversion {
    private val CONVERTERS_MAP = TypedOptionConverterMap()

    fun <T> getConverter(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>): (Any?) -> T? {
        return CONVERTERS_MAP[aes]
    }

    fun <T> apply(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>, optionValue: Any): T? {
        val converter = getConverter(aes)
        return converter(optionValue)
    }

    fun <T> applyToList(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>, optionValues: List<*>): List<T?> {
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
    fun has(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
        return CONVERTERS_MAP.containsKey(aes)
    }
}
