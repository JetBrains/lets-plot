/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors.solid
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.GeomKind.*
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.render.point.NamedShape
import jetbrains.datalore.plot.base.render.point.TinyPointShape

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
        return if (solid(color)) {
            color.changeAlpha(intAlpha)
        } else color
    }

    fun createColorMarkerMapper(
        geomKind: GeomKind?,
        ctx: GeomContext,
    ): (DataPointAesthetics) -> List<Color> {
        return createColorMarkerMapper(
            geomKind,
            ctx.isMappedAes(Aes.FILL),
            ctx.isMappedAes(Aes.COLOR),
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
        isMappedFill: Boolean,
        isMappedColor: Boolean
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

        val colorBars: List<(DataPointAesthetics) -> Color?> = when (geomKind) {
            null ->
                listOfNotNull(
                    fillColorGetter.takeIf { isMappedFill },
                    strokeColorGetter.takeIf { isMappedColor },
                )

            PATH, CONTOUR, DENSITY2D, FREQPOLY, LINE, STEP, H_LINE, V_LINE, SEGMENT, SMOOTH ->
                listOf(strokeColorGetter) // show even without mapping (usecase - layers with const color)

            DENSITY -> when {
                !isMappedFill -> listOf(strokeColorGetter)
                else -> listOfNotNull(
                    fillColorGetter.takeIf { isMappedFill },
                    strokeColorGetter.takeIf { isMappedColor },
                )
            }

            POINT -> {
                // For solid points: Color is used as fill
                val pointFillColorGetter = { p: DataPointAesthetics ->
                    val shape = p.shape()!!
                    val isMapped = if (shape is NamedShape && shape.isSolid) isMappedColor else isMappedFill
                    fillColorGetter(p).takeIf { isMapped }
                }
                listOfNotNull(
                    pointFillColorGetter,
                    strokeColorGetter.takeIf { isMappedColor },
                )
            }

            else ->
                listOfNotNull(
                    fillColorGetter.takeIf { isMappedFill && Aes.FILL in GeomMeta.renders(geomKind) },
                    strokeColorGetter.takeIf { isMappedColor && Aes.COLOR in GeomMeta.renders(geomKind) },
                )
        }

        return { p: DataPointAesthetics -> colorBars.mapNotNull { it(p) } }
    }
}
