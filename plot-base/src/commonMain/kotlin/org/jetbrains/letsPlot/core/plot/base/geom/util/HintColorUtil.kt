/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.GeomKind.*
import org.jetbrains.letsPlot.core.plot.base.GeomMeta
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape
import org.jetbrains.letsPlot.core.plot.base.render.point.TinyPointShape
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipMarker

object HintColorUtil {
    fun colorWithAlpha(p: DataPointAesthetics): Color {
        return AestheticsUtil.resolveColor(p, applyAlpha = true)
    }

    fun createColorMarkerMapper(
        ctx: GeomContext,
    ): (DataPointAesthetics) -> TooltipMarker {
        return createColorMarkerMapper(
            ctx.geomKind(),
            isMappedFill = { p: DataPointAesthetics -> ctx.isMappedAes(p.fillAes) },
            isMappedColor = { p: DataPointAesthetics -> ctx.isMappedAes(p.colorAes) }
        )
    }

    private fun pointFillMapper(p: DataPointAesthetics): Color =
        when (val shape = p.shape()) {
            is NamedShape -> when {
                shape.isFilled -> AestheticsUtil.resolveFill(p)
                shape.isSolid -> AestheticsUtil.resolveColor(p, applyAlpha = true)
                else -> Color.TRANSPARENT
            }
            TinyPointShape -> AestheticsUtil.resolveColor(p, applyAlpha = true)
            else -> Color.TRANSPARENT
        }

    private fun pointStrokeMapper(p: DataPointAesthetics): Color {
        return when (val shape = p.shape()) {
            is NamedShape -> {
                when {
                    shape.isSolid -> Color.TRANSPARENT
                    shape.isFilled -> p.color()!!
                    else -> colorWithAlpha(p)
                }
            }
            TinyPointShape -> p.color()!!
            else ->  Color.TRANSPARENT
        }
    }

    fun createColorMarkerMapper(
        geomKind: GeomKind,
        isMappedFill: (DataPointAesthetics) -> Boolean,
        isMappedColor: (DataPointAesthetics) -> Boolean
    ): (DataPointAesthetics) -> TooltipMarker {
        val fillColorGetter: (DataPointAesthetics) -> Color? = when (geomKind) {
            SINA, POINT -> this::pointFillMapper
            else -> { p: DataPointAesthetics -> AestheticsUtil.resolveFill(p) }
        }.let { fillSelector -> { p: DataPointAesthetics ->
            fillSelector(p).takeIf { it.alpha > 0 } }
        }

        val strokeColorGetter: (DataPointAesthetics) -> Color? = when (geomKind) {
            ERROR_BAR, H_LINE, V_LINE, LINE_RANGE, PATH, POINT_RANGE, TEXT, TEXT_REPEL, LABEL_REPEL, TILE -> HintColorUtil::colorWithAlpha
            SINA, POINT -> this::pointStrokeMapper
            else -> DataPointAesthetics::color // border always ignores alpha
        }.let { colorSelector -> { p: DataPointAesthetics ->
            colorSelector(p)?.takeIf { it.alpha > 0 && p.size() != 0.0 } }
        }

        return { p: DataPointAesthetics ->
            when (geomKind) {
                PATH, CONTOUR, DENSITY2D, FREQPOLY, LINE, STEP, H_LINE, V_LINE, SEGMENT, CURVE, SPOKE, SMOOTH ->
                    TooltipMarker.create(majorColor = strokeColorGetter(p)) // show even without mapping (usecase - layers with const color)

                DENSITY -> when {
                    !isMappedFill(p) -> TooltipMarker.create(majorColor = strokeColorGetter(p))
                    else -> TooltipMarker.create(
                        majorColor = fillColorGetter(p).takeIf { isMappedFill(p) },
                        minorColor = strokeColorGetter(p).takeIf { isMappedColor(p) },
                    )
                }

                SINA, POINT -> {
                    // For solid points: Color is used as fill
                    val shape = p.shape()!!
                    val isMapped = if (shape is NamedShape && shape.isSolid) isMappedColor else isMappedFill
                    TooltipMarker.create(
                        majorColor = fillColorGetter(p).takeIf { isMapped(p) },
                        minorColor = strokeColorGetter(p).takeIf { isMappedColor(p) },
                    )
                }

                else -> {
                    val renderedAes = GeomMeta.renders(geomKind, p.colorAes, p.fillAes)
                    TooltipMarker.create(
                        majorColor = fillColorGetter(p).takeIf { isMappedFill(p) && p.fillAes in renderedAes },
                        minorColor = strokeColorGetter(p).takeIf { isMappedColor(p) && p.colorAes in renderedAes }
                    )
                }
            }
        }
    }
}
