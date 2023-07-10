/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.Scale

class TestingPlotContextWithTooltipFormatters : PlotContext {
    private val mockFormatters: MutableMap<Aes<*>, (Any?) -> String> = HashMap()

    override val layers: List<PlotContext.Layer>
        get() = UNSUPPORTED("Not yet implemented")

    override fun hasScale(aes: Aes<*>): Boolean {
        UNSUPPORTED("Not yet implemented")
    }

    override fun getScale(aes: Aes<*>): Scale {
        UNSUPPORTED("Not yet implemented")
    }

    override fun overallTransformedDomain(aes: Aes<*>): DoubleSpan {
        UNSUPPORTED("Not yet implemented")
    }

    override fun getTooltipFormatter(aes: Aes<*>, defaultValue: () -> (Any?) -> String): (Any?) -> String {
        return mockFormatters.getValue(aes)
    }

    internal fun addMappedData(mapping: MappedDataAccessMock.Mapping<*>) {
        val formatter = { _: Any? -> mapping.value }
        mockFormatters[mapping.aes] = formatter
    }
}