/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.tooltip.FormatterProvider

class TestingTooltipFormatters : FormatterProvider {
    private val mockFormatters: MutableMap<Aes<*>, (Any?) -> String> = HashMap()

    override fun getFormatter(aes: Aes<*>): (Any?) -> String {
        return mockFormatters.getValue(aes)
    }

    internal fun addMappedData(mapping: MappedDataAccessMock.Mapping<*>) {
        val formatter = { _: Any? -> mapping.value }
        mockFormatters[mapping.aes] = formatter
    }
}