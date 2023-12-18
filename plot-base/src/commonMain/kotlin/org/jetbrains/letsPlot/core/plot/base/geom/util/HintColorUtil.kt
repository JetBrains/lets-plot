/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.GeomKind.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesInitValue
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape
import org.jetbrains.letsPlot.core.plot.base.render.point.TinyPointShape

object HintColorUtil {
    fun colorWithAlpha(p: DataPointAesthetics): Color {
        return applyAlpha(
            p.color()!!,
            p.alpha()!!
        )
    }

    fun fillWithAlpha(p: DataPointAesthetics): Color {
        return applyAlpha(
            p.fill()!!,
            p.alpha()!!
        )
    }

    fun applyAlpha(color: Color, alpha: Double): Color {
        val intAlpha = (255 * alpha).toInt()
        return if (alpha != AesInitValue.DEFAULT_ALPHA) {
            color.changeAlpha(intAlpha)
        } else {
            color
        }
    }

    fun createColorMarkerMapper(
        geomKind: GeomKind?,
        ctx: GeomContext,
    ): (DataPointAesthetics) -> List<Color> {
        return createColorMarkerMapper(
            geomKind,
            isMappedFill = { p: DataPointAesthetics -> ctx.isMappedAes(p.fillAes) },
            isMappedColor = { p: DataPointAesthetics -> ctx.isMappedAes(p.colorAes) }
        )
    }

    private fun pointFillMapper(p:DataPointAesthetics): Color =
        when (val shape = p.shape()) {
            is NamedShape -> applyAlpha(
                AestheticsUtil.fill(shape.isFilled, shape.isSolid, p),
                p.alpha()!!
            )
            TinyPointShape -> p.color()!!
            else -> Color.TRANSPARENT
        }

    private fun pointStrokeMapper(p:DataPointAesthetics): Color {
        return when(val shape = p.shape()) {
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
        geomKind: GeomKind?,
        isMappedFill: (DataPointAesthetics) -> Boolean,
        isMappedColor: (DataPointAesthetics) -> Boolean
    ): (DataPointAesthetics) -> List<Color> {
        val fillColorGetter: (DataPointAesthetics) -> Color? = when (geomKind) {
            POINT -> this::pointFillMapper
            else -> this::fillWithAlpha
        }.let { fillSelector -> { p: DataPointAesthetics ->
            fillSelector(p).takeIf { it.alpha > 0 } }
        }

        val strokeColorGetter: (DataPointAesthetics) -> Color? = when (geomKind) {
            ERROR_BAR, H_LINE, V_LINE, LINE_RANGE, PATH, POINT_RANGE, TEXT, TILE -> HintColorUtil::colorWithAlpha
            POINT -> this::pointStrokeMapper
            else -> DataPointAesthetics::color // border always ignores alpha
        }.let { colorSelector -> { p: DataPointAesthetics ->
            colorSelector(p)?.takeIf { it.alpha > 0 && p.size() != 0.0 } }
        }

        return { p: DataPointAesthetics ->
            when (geomKind) {
                null -> listOf(
                    // should be mapped and visible
                    fillColorGetter(p).takeIf { isMappedFill(p) },
                    strokeColorGetter(p).takeIf { isMappedColor(p) },
                )

                PATH, CONTOUR, DENSITY2D, FREQPOLY, LINE, STEP, H_LINE, V_LINE, SEGMENT, SPOKE, SMOOTH ->
                    listOf(strokeColorGetter(p)) // show even without mapping (usecase - layers with const color)

                DENSITY -> when {
                    !isMappedFill(p) -> listOf(strokeColorGetter(p))
                    else -> listOf(
                        // should be mapped and visible
                        fillColorGetter(p).takeIf { isMappedFill(p) },
                        strokeColorGetter(p).takeIf { isMappedColor(p) },
                    )
                }

                POINT -> {
                    // For solid points: Color is used as fill
                    val shape = p.shape()!!
                    val isMapped = if (shape is NamedShape && shape.isSolid) isMappedColor else isMappedFill
                    listOf(
                        fillColorGetter(p).takeIf { isMapped(p) },
                        strokeColorGetter(p).takeIf { isMappedColor(p) },
                    )
                }

                else -> {
                    val renderedAes = GeomMeta.renders(geomKind, p.colorAes, p.fillAes)
                    listOf(
                        fillColorGetter(p).takeIf { isMappedFill(p) && p.fillAes in renderedAes },
                        strokeColorGetter(p).takeIf { isMappedColor(p) && p.colorAes in renderedAes }
                    )
                }
            }
                .filterNotNull()

        }
    }
}
