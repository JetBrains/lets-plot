/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.Scale

class TestingPlotContextWithTooltipFormatters : PlotContext {

    override val superscriptExponent: Boolean = false

    override fun hasScale(aes: Aes<*>): Boolean {
        UNSUPPORTED("Not yet implemented")
    }

    override fun getScale(aes: Aes<*>): Scale {
        UNSUPPORTED("Not yet implemented")
    }

    override fun overallTransformedDomain(aes: Aes<*>): DoubleSpan {
        UNSUPPORTED("Not yet implemented")
    }

    override fun getTooltipFormatter(aes: Aes<*>): (Any?) -> String {
        return Any?::toString
    }
}