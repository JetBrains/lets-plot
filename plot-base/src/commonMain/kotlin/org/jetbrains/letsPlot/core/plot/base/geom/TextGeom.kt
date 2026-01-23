/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesInitValue.DEFAULT_ALPHA
import org.jetbrains.letsPlot.core.plot.base.aes.AesInitValue.DEFAULT_SEGMENT_COLOR
import org.jetbrains.letsPlot.core.plot.base.geom.util.DataPointAestheticsDelegate
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text

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

        val textHelper = TextHelper(aesthetics, pos, coord, ctx, formatter, naValue, sizeUnit, checkOverlap, ::coordOrNull, ::objectRectangle, ::componentFactory)
        textHelper.createSvgComponents().forEach { svgElement ->
            root.add(svgElement)
        }
        textHelper.buildHints(targetCollector)
    }

    open fun coordOrNull(p: DataPointAesthetics): DoubleVector? = p.finiteVectorOrNull(Aes.X, Aes.Y)

    open fun componentFactory(
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        sizeUnitRatio: Double,
        ctx: GeomContext,
        boundsCenter: DoubleVector?
    ) = TextHelper.textComponentFactory(p, location, text, sizeUnitRatio, ctx, boundsCenter)

    open fun objectRectangle(
        location: DoubleVector,
        textSize: DoubleVector,
        fontSize: Double,
        hAnchor: Text.HorizontalAnchor,
        vAnchor: Text.VerticalAnchor,
    ) = TextHelper.textRectangle(location, textSize, hAnchor, vAnchor)

    companion object {
        const val DEF_NA_VALUE = "n/a"
        const val HANDLES_GROUPS = false

        // Current implementation works for label_format ='.2f'
        // and values between -1.0 and 1.0.
        const val BASELINE_TEXT_WIDTH = 6.0

        internal fun toSegmentAes(p: DataPointAesthetics): DataPointAesthetics {
            return object : DataPointAestheticsDelegate(p) {

                override operator fun <T> get(aes: Aes<T>): T? {
                    val value: Any? = when (aes) {
                        Aes.COLOR -> if (super.get(Aes.SEGMENT_COLOR) == DEFAULT_SEGMENT_COLOR) super.get(Aes.COLOR) else super.get(Aes.SEGMENT_COLOR)
                        Aes.SIZE -> super.get(Aes.SEGMENT_SIZE)
                        Aes.ALPHA -> if (super.get(Aes.SEGMENT_ALPHA) == DEFAULT_ALPHA) super.get(Aes.ALPHA) else super.get(Aes.SEGMENT_ALPHA)
                        else -> super.get(aes)
                    }
                    @Suppress("UNCHECKED_CAST")
                    return value as T?
                }
            }
        }
    }
}

// How 'just' and 'angle' works together
// https://stackoverflow.com/questions/7263849/what-do-hjust-and-vjust-do-when-making-a-plot-using-ggplot