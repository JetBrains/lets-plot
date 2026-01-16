/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextUtil
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement

open class TextGeom : GeomBase() {
    var formatter: ((Any) -> String)? = null
    var naValue = DEF_NA_VALUE
    var sizeUnit: String? = null
    var checkOverlap: Boolean = false

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = TextLegendKeyElementFactory()

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val targetCollector = getGeomTargetCollector(ctx)

        val textHelper = TextHelper(aesthetics, pos, coord, ctx, formatter, naValue, sizeUnit, checkOverlap, ::componentFactory)
        textHelper.createSvgComponents().forEach { svgElement ->
            root.add(svgElement)
        }
        textHelper.buildHints(targetCollector)
    }

    open fun componentFactory(
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        sizeUnitRatio: Double,
        ctx: GeomContext,
        boundsCenter: DoubleVector?
    ): SvgGElement {
        return TextHelper.textComponentFactory(p, location, text, sizeUnitRatio, ctx, boundsCenter)
    }

    // TODO: Delete after refactor
    open fun objectRectangle(
        location: DoubleVector,
        textSize: DoubleVector,
        fontSize: Double,
        hAnchor: Text.HorizontalAnchor,
        vAnchor: Text.VerticalAnchor,
    ) = TextUtil.rectangleForText(location, textSize, padding = 0.0, hAnchor, vAnchor)

    // TODO: Delete after refactor
    fun getRect(
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        sizeUnitRatio: Double,
        ctx: GeomContext,
        boundsCenter: DoubleVector?
    ): List<DoubleVector> {
        val textSize = TextUtil.measure(text, p, ctx, sizeUnitRatio)
        val hAnchor = TextUtil.hAnchor(p, location, boundsCenter)
        val vAnchor = TextUtil.vAnchor(p, location, boundsCenter)
        val fontSize = TextUtil.fontSize(p, sizeUnitRatio)
        val angle = toRadians(TextUtil.angle(p))

        return objectRectangle(location, textSize, fontSize, hAnchor, vAnchor)
            .rotate(angle, location)
    }

    // TODO: Delete after refactor
    fun toString(label: Any?, geomContext: GeomContext): String {
        if (label == null) return naValue

        val formatter = formatter ?: geomContext.getDefaultFormatter(Aes.LABEL)
        return formatter(label)
    }

    companion object {
        const val DEF_NA_VALUE = "n/a"
        const val HANDLES_GROUPS = false

        // Current implementation works for label_format ='.2f'
        // and values between -1.0 and 1.0.
        const val BASELINE_TEXT_WIDTH = 6.0
    }
}

// How 'just' and 'angle' works together
// https://stackoverflow.com/questions/7263849/what-do-hjust-and-vjust-do-when-making-a-plot-using-ggplot