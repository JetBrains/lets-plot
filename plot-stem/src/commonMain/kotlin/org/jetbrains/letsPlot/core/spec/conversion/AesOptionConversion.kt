/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.conversion

import org.jetbrains.letsPlot.core.plot.base.Aes

class AesOptionConversion(colorConverter: ColorOptionConverter) {
    private val converterMap = TypedOptionConverterMap(colorConverter)

    fun <T> getConverter(aes: Aes<T>): (Any?) -> T? {
        return converterMap[aes]
    }

    fun <T> apply(aes: Aes<T>, optionValue: Any?): T? {
        val converter = getConverter(aes)
        return converter(optionValue)
    }

    fun <T> applyToList(aes: Aes<T>, optionValues: List<*>): List<T?> {
        return optionValues.map { apply(aes, it) }
    }

    companion object {
        val demoAndTest: AesOptionConversion = AesOptionConversion(ColorOptionConverter.demoAndTest)
    }
}
