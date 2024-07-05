/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TooltipFormatting

internal class PlotAssemblerPlotContext constructor(
    private val geomTiles: PlotGeomTiles,
    override val superscriptExponent: Boolean
) : PlotContext {

    private val tooltipFormatters: MutableMap<Aes<*>, (Any?) -> String> = HashMap()

    override fun hasScale(aes: Aes<*>) = geomTiles.scalesBeforeFacets.containsKey(aes)

    override fun getScale(aes: Aes<*>): Scale {
        checkPositionalAes(aes)
        return geomTiles.scalesBeforeFacets.getValue(aes)
    }

    override fun overallTransformedDomain(aes: Aes<*>): DoubleSpan {
        return geomTiles.overallTransformedDomain(aes)
    }

    override fun getTooltipFormatter(aes: Aes<*>): (Any?) -> String {
        checkPositionalAes(aes)
        return tooltipFormatters.getOrPut(aes) {
            TooltipFormatting.createFormatter(aes, this)
        }
    }

    override fun getDefaultFormatter(aes: Aes<*>): (Any) -> String {
        return geomTiles.defaultFormatters[aes]
            ?: Any::toString
    }

    override fun getDefaultFormatter(varName: String): (Any) -> String {
        return geomTiles.defaultFormatters[varName]
            ?: Any::toString
    }


    private companion object {
        fun checkPositionalAes(aes: Aes<*>) {
            // expect only X,Y or not positional
            check(!Aes.isPositionalXY(aes) || aes == Aes.X || aes == Aes.Y) {
                "Positional aesthetic should be either X or Y but was $aes"
            }
        }
    }
}
