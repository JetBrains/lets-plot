/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.common.data.SeriesUtil

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
        for (p in aesthetics.dataPoints()) {
            val x = p.x()
            val y = p.y()
            val text = toString(p.label())
            if (SeriesUtil.allFinite(x, y) && text.isNotEmpty()) {
                val point = DoubleVector(x!!, y!!)
                val loc = helper.toClient(point, p)
                if (loc == null) continue

                // Adapt point size to plot 'grid step' if necessary (i.e. in correlation matrix).
                val sizeUnitRatio = when (sizeUnit) {
                    null -> 1.0
                    else -> getSizeUnitRatio(point, coord, sizeUnit!!)
                }

                buildTextComponent(root, p, loc, text, sizeUnitRatio, ctx)

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
        root: SvgRoot,
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        sizeUnitRatio: Double,
        ctx: GeomContext
    ) {
        val label = TextLabel(text)
        GeomHelper.decorate(label, p, sizeUnitRatio, applyAlpha = true)
        label.moveTo(location)
        root.add(label.rootGroup)
    }

    private fun toString(label: Any?): String {
        return when {
            label == null -> naValue
            formatter != null -> formatter!!.invoke(label)
            else -> label.toString()
        }
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
// ToDo: lineheight (aes)
// ToDo: nudge_x, nudge_y

