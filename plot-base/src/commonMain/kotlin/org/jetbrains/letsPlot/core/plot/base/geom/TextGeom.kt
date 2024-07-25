/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextUtil
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils

open class TextGeom : GeomBase() {
    var formatter: ((Any) -> String)? = null
    var naValue = DEF_NA_VALUE
    var sizeUnit: String? = null

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = TextLegendKeyElementFactory()

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coord, ctx)
        val targetCollector = getGeomTargetCollector(ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.TEXT, ctx)
        val aesBoundsCenter = coord.toClient(ctx.getAesBounds())?.center

        for (p in aesthetics.dataPoints()) {
            val x = p.x()
            val y = p.y()
            val text = toString(p.label(), ctx)
            if (SeriesUtil.allFinite(x, y) && text.isNotEmpty()) {
                val point = DoubleVector(x!!, y!!)
                val loc = helper.toClient(point, p)
                if (loc == null) continue

                // Adapt point size to plot 'grid step' if necessary (i.e. in correlation matrix).
                val sizeUnitRatio = when (sizeUnit) {
                    null -> 1.0
                    else -> getSizeUnitRatio(point, coord, sizeUnit!!)
                }

                val g = buildTextComponent(p, loc, text, sizeUnitRatio, ctx, aesBoundsCenter)
                root.add(g)

                // The geom_text tooltip is similar to the geom_tile:
                // it looks better when the text is on a tile in corr_plot (but the color will be different from the geom_tile tooltip)
                targetCollector.addPoint(
                    p.index(),
                    loc,
                    sizeUnitRatio * AesScaling.textSize(p) / 2,
                    GeomTargetCollector.TooltipParams(
                        markerColors = colorsByDataPoint(p)
                    ),
                    TipLayoutHint.Kind.CURSOR_TOOLTIP
                )
            }
        }
    }

    open fun buildTextComponent(
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        sizeUnitRatio: Double,
        ctx: GeomContext,
        boundsCenter: DoubleVector?
    ): SvgGElement {
        val label = MultilineLabel(text)
        TextUtil.decorate(label, p, sizeUnitRatio, applyAlpha = true)
        val hAnchor = TextUtil.hAnchor(p, location, boundsCenter)
        label.setHorizontalAnchor(hAnchor)

        val fontSize = TextUtil.fontSize(p, sizeUnitRatio)
        val textHeight = TextUtil.measure(text, p, ctx, sizeUnitRatio).y
        //val textHeight = TextHelper.lineheight(p, sizeUnitRatio) * (label.linesCount() - 1) + fontSize

        val yPosition = when (TextUtil.vAnchor(p, location, boundsCenter)) {
            Text.VerticalAnchor.TOP -> location.y + fontSize * 0.7
            Text.VerticalAnchor.BOTTOM -> location.y - textHeight + fontSize
            Text.VerticalAnchor.CENTER -> location.y - textHeight / 2 + fontSize * 0.8
        }

        val textLocation = DoubleVector(location.x, yPosition)
        label.moveTo(textLocation)

        val g = SvgGElement()
        g.children().add(label.rootGroup)
        SvgUtils.transformRotate(g, TextUtil.angle(p), location.x, location.y)
        return g
    }

    private fun toString(label: Any?, geomContext: GeomContext): String {
        if (label == null) return naValue

        val formatter = formatter ?: geomContext.getDefaultFormatter(Aes.LABEL)
        return formatter(label)
    }

    companion object {
        const val DEF_NA_VALUE = "n/a"
        const val HANDLES_GROUPS = false

        // Current implementation works for label_format ='.2f'
        // and values between -1.0 and 1.0.
        private const val BASELINE_TEXT_WIDTH = 6.0

        private fun getSizeUnitRatio(
            p: DoubleVector,
            coord: CoordinateSystem,
            axis: String
        ): Double {
            val unitSquareSize = coord.unitSize(p)
            val unitSize = when (axis.lowercase()) {
                "x" -> unitSquareSize.x
                "y" -> unitSquareSize.y
                else -> error("Size unit value must be either 'x' or 'y', but was $axis.")
            }
            return unitSize / BASELINE_TEXT_WIDTH
        }
    }
}

// How 'just' and 'angle' works together
// https://stackoverflow.com/questions/7263849/what-do-hjust-and-vjust-do-when-making-a-plot-using-ggplot