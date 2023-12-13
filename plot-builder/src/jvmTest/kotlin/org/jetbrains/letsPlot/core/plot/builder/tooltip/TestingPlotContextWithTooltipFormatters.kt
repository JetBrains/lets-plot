/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.Scale

class TestingPlotContextWithTooltipFormatters : PlotContext {
    private val mockFormatters: MutableMap<org.jetbrains.letsPlot.core.plot.base.Aes<*>, (Any?) -> String> = HashMap()

    override val superscriptExponent: Boolean = false

    override val layers: List<PlotContext.Layer>
        get() = UNSUPPORTED("Not yet implemented")

    override fun hasScale(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
        UNSUPPORTED("Not yet implemented")
    }

    override fun getScale(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Scale {
        UNSUPPORTED("Not yet implemented")
    }

    override fun overallTransformedDomain(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): DoubleSpan {
        UNSUPPORTED("Not yet implemented")
    }

    override fun getTooltipFormatter(
        aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>,
        defaultValue: () -> (Any?) -> String
    ): (Any?) -> String {
        return mockFormatters.getValue(aes)
    }

    internal fun addMappedData(mapping: MappedDataAccessMock.Mapping<*>) {
        val formatter = { _: Any? -> mapping.value }
        mockFormatters[mapping.aes] = formatter
    }
}