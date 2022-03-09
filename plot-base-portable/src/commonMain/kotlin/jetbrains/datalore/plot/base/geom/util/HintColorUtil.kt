/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors.solid
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext

object HintColorUtil {
    fun fromColor(p: DataPointAesthetics): Color {
        return fromColorValue(
            p.color()!!,
            p.alpha()!!
        )
    }

    fun fromFill(p: DataPointAesthetics): Color {
        return fromColorValue(
            p.fill()!!,
            p.alpha()!!
        )
    }

    fun fromColorValue(color: Color, alpha: Double): Color {
        val intAlpha = (255 * alpha).toInt()
        return if (solid(color)) {
            color.changeAlpha(intAlpha)
        } else color
    }

    fun fromMappedAndVisibleColors(
        ctx: GeomContext,
        fillFactory: (DataPointAesthetics) -> Color?,
        strokeFactory: (DataPointAesthetics) -> Color?
    ): (DataPointAesthetics) -> List<Color> {
        val isMappedFill = ctx.isMappedAes(Aes.FILL)
        val isMappedColor = ctx.isMappedAes(Aes.COLOR)
        return { p: DataPointAesthetics ->
            listOfNotNull(
                // should be mapped and visible
                fillFactory(p).takeIf { isMappedFill && p.alpha()!! > 0 },
                strokeFactory(p).takeIf { isMappedColor && p.size()!! > 0 },
            )
        }
    }
}
