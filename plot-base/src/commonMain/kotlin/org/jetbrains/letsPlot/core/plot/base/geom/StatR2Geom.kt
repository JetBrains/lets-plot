/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextUtil.fontFamily
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.Label
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils
import kotlin.math.max

class StatR2Geom : GeomBase() {
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

        for ((i, dp) in aesthetics.dataPoints().withIndex()) {
            val sizeUnitRatio = AesScaling.sizeUnitRatio(DoubleVector.ZERO, coord, null,
                TextGeom.Companion.BASELINE_TEXT_WIDTH
            )

            val label = buildTextComponent(i, dp, toString(dp.label(), ctx), sizeUnitRatio, coord, ctx)
            root.add(label)
        }
    }

    fun toString(label: Any?, geomContext: GeomContext): String {
        if (label == null) return ""

        val formatter = geomContext.getDefaultFormatter(Aes.LABEL)
        return formatter(label)
    }

    private fun buildTextComponent(
        index: Int,
        p: DataPointAesthetics,
        text: String,
        sizeUnitRatio: Double,
        coord: CoordinateSystem,
        ctx: GeomContext,
    ): SvgGElement {
        val viewPort = coord.toClient(overallAesBounds(ctx))!!

        val label = Label(text)
        TextUtil.decorate(label, p, sizeUnitRatio, applyAlpha = false)

        val measure = measure(text, p, ctx, 10.0)

        label.setFontSize(10.0)
        label.setLineHeight(12.0)
        label.setHorizontalAnchor(Text.HorizontalAnchor.RIGHT)
        label.setVerticalAnchor(Text.VerticalAnchor.BOTTOM)

        val location = DoubleVector(
            viewPort.right - 20,
            viewPort.bottom - measure.y * index - 20
        )

        label.moveTo(location)

        val g = SvgGElement()
        g.children().add(label.rootGroup)
        SvgUtils.transformRotate(g, TextUtil.angle(p), location.x, location.y)
        return g
    }

    companion object {
        const val DEF_NA_VALUE = "n/a"
        const val HANDLES_GROUPS = false

        // Current implementation works for label_format ='.2f'
        // and values between -1.0 and 1.0.
        const val BASELINE_TEXT_WIDTH = 6.0


        fun measure(text: String, p: DataPointAesthetics, ctx: GeomContext, fontSize: Double = 10.0): DoubleVector {
            val lines = Label.splitLines(text)
            val lineHeight = 12.0
            val fontFamily = fontFamily(p)
            val fontFace = FontFace.fromString(p.fontface())

            val estimated = lines.map { line ->
                ctx.estimateTextSize(line, fontFamily, fontSize, fontFace.bold, fontFace.italic)
            }.fold(DoubleVector.ZERO) { acc, sz ->
                DoubleVector(
                    x = max(acc.x, sz.x),
                    y = acc.y + sz.y
                )
            }
            val lineInterval = lineHeight - fontSize
            val textHeight = estimated.y + lineInterval * (lines.size - 1)
            return DoubleVector(estimated.x, textHeight)
        }
    }
}
