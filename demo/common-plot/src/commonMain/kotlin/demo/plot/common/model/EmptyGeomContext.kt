/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.NullPlotContext
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.Annotation
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.NullGeomTargetCollector
import org.jetbrains.letsPlot.core.plot.builder.presentation.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.builder.presentation.PlotLabelSpec

/**
 * Used in demos only.
 */
class EmptyGeomContext : GeomContext {
    override val flipped: Boolean = false
    override val targetCollector: GeomTargetCollector = NullGeomTargetCollector()
    override val annotation: Annotation? = null
    override val backgroundColor: Color = Color.WHITE
    override val plotContext: PlotContext = NullPlotContext

    override fun getResolution(aes: Aes<Double>): Double {
        throw IllegalStateException("Not available in an empty geom context")
    }

    override fun getAesBounds(): DoubleRectangle {
        throw IllegalStateException("Not available in an empty geom context")
    }

    override fun withTargetCollector(targetCollector: GeomTargetCollector): GeomContext {
        throw IllegalStateException("Not available in an empty geom context")
    }


    override fun getDefaultFormatter(aes: Aes<*>): (Any) -> String {
        throw IllegalStateException("Not available in an empty geom context")
    }

    override fun getDefaultFormatter(varName: String): (Any) -> String {
        throw IllegalStateException("Not available in an empty geom context")
    }

    override fun isMappedAes(aes: Aes<*>): Boolean = false
    override fun estimateTextSize(
        text: String,
        family: String,
        size: Double,
        isBold: Boolean,
        isItalic: Boolean
    ): DoubleVector {
        @Suppress("NAME_SHADOWING")
        val family = DefaultFontFamilyRegistry().get(family)
        return PlotLabelSpec(
            Font(
                family = family,
                size = size.toInt(),
                isBold = isBold,
                isItalic = isItalic
            ),
        ).dimensions(text)
    }
}