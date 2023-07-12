/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import org.jetbrains.letsPlot.core.commons.typedKey.TypedKey
import org.jetbrains.letsPlot.core.commons.typedKey.TypedKeyHashMap
import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aes.Companion.ALPHA
import jetbrains.datalore.plot.base.Aes.Companion.ANGLE
import jetbrains.datalore.plot.base.Aes.Companion.BINWIDTH
import jetbrains.datalore.plot.base.Aes.Companion.COLOR
import jetbrains.datalore.plot.base.Aes.Companion.EXPLODE
import jetbrains.datalore.plot.base.Aes.Companion.FAMILY
import jetbrains.datalore.plot.base.Aes.Companion.FILL
import jetbrains.datalore.plot.base.Aes.Companion.FLOW
import jetbrains.datalore.plot.base.Aes.Companion.FONTFACE
import jetbrains.datalore.plot.base.Aes.Companion.FRAME
import jetbrains.datalore.plot.base.Aes.Companion.HEIGHT
import jetbrains.datalore.plot.base.Aes.Companion.HJUST
import jetbrains.datalore.plot.base.Aes.Companion.INTERCEPT
import jetbrains.datalore.plot.base.Aes.Companion.LABEL
import jetbrains.datalore.plot.base.Aes.Companion.LINEHEIGHT
import jetbrains.datalore.plot.base.Aes.Companion.LINETYPE
import jetbrains.datalore.plot.base.Aes.Companion.LOWER
import jetbrains.datalore.plot.base.Aes.Companion.MAP_ID
import jetbrains.datalore.plot.base.Aes.Companion.MIDDLE
import jetbrains.datalore.plot.base.Aes.Companion.PAINT_A
import jetbrains.datalore.plot.base.Aes.Companion.PAINT_B
import jetbrains.datalore.plot.base.Aes.Companion.PAINT_C
import jetbrains.datalore.plot.base.Aes.Companion.SAMPLE
import jetbrains.datalore.plot.base.Aes.Companion.QUANTILE
import jetbrains.datalore.plot.base.Aes.Companion.SHAPE
import jetbrains.datalore.plot.base.Aes.Companion.SIZE
import jetbrains.datalore.plot.base.Aes.Companion.STROKE
import jetbrains.datalore.plot.base.Aes.Companion.LINEWIDTH
import jetbrains.datalore.plot.base.Aes.Companion.SLICE
import jetbrains.datalore.plot.base.Aes.Companion.SLOPE
import jetbrains.datalore.plot.base.Aes.Companion.SPEED
import jetbrains.datalore.plot.base.Aes.Companion.STACKSIZE
import jetbrains.datalore.plot.base.Aes.Companion.UPPER
import jetbrains.datalore.plot.base.Aes.Companion.VIOLINWIDTH
import jetbrains.datalore.plot.base.Aes.Companion.VJUST
import jetbrains.datalore.plot.base.Aes.Companion.WEIGHT
import jetbrains.datalore.plot.base.Aes.Companion.WIDTH
import jetbrains.datalore.plot.base.Aes.Companion.X
import jetbrains.datalore.plot.base.Aes.Companion.XEND
import jetbrains.datalore.plot.base.Aes.Companion.XINTERCEPT
import jetbrains.datalore.plot.base.Aes.Companion.XMAX
import jetbrains.datalore.plot.base.Aes.Companion.XMIN
import jetbrains.datalore.plot.base.Aes.Companion.Y
import jetbrains.datalore.plot.base.Aes.Companion.YEND
import jetbrains.datalore.plot.base.Aes.Companion.YINTERCEPT
import jetbrains.datalore.plot.base.Aes.Companion.YMAX
import jetbrains.datalore.plot.base.Aes.Companion.YMIN
import jetbrains.datalore.plot.base.Aes.Companion.Z
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.render.linetype.NamedLineType
import jetbrains.datalore.plot.base.render.point.TinyPointShape

object DefaultNaValue {
    private val VALUE_MAP = TypedKeyHashMap()

    init {
        VALUE_MAP.put(X, 0.0)
        VALUE_MAP.put(Y, 0.0)
        VALUE_MAP.put(Z, 0.0)
        VALUE_MAP.put(YMIN, 0.0)
        VALUE_MAP.put(YMAX, 0.0)
        VALUE_MAP.put(COLOR, Color.GRAY)
        VALUE_MAP.put(FILL, Color.GRAY)
        VALUE_MAP.put(PAINT_A, Color.GRAY)
        VALUE_MAP.put(PAINT_B, Color.GRAY)
        VALUE_MAP.put(PAINT_C, Color.GRAY)
        VALUE_MAP.put(ALPHA, 0.0)
        VALUE_MAP.put(SHAPE, TinyPointShape)
        VALUE_MAP.put(LINETYPE, NamedLineType.SOLID)
        VALUE_MAP.put(SIZE, AesScaling.sizeFromCircleDiameter(1.0))
        VALUE_MAP.put(STROKE, 1.0)
        VALUE_MAP.put(LINEWIDTH, 1.0)
        VALUE_MAP.put(STACKSIZE, 0.0)
        VALUE_MAP.put(WIDTH, 1.0)
        VALUE_MAP.put(HEIGHT, 1.0)
        VALUE_MAP.put(BINWIDTH, 0.0)
        VALUE_MAP.put(VIOLINWIDTH, 0.0)
        VALUE_MAP.put(WEIGHT, 1.0)
        VALUE_MAP.put(INTERCEPT, 0.0)
        VALUE_MAP.put(SLOPE, 1.0)
        VALUE_MAP.put(XINTERCEPT, 0.0)
        VALUE_MAP.put(YINTERCEPT, 0.0)
        VALUE_MAP.put(LOWER, 0.0)
        VALUE_MAP.put(MIDDLE, 0.0)
        VALUE_MAP.put(UPPER, 0.0)
        VALUE_MAP.put(SAMPLE, 0.0)
        VALUE_MAP.put(QUANTILE, 0.0)
        VALUE_MAP.put(MAP_ID, "empty map_id")
        VALUE_MAP.put(FRAME, "empty frame")
        VALUE_MAP.put(SPEED, 10.0)
        VALUE_MAP.put(FLOW, 0.1)
        VALUE_MAP.put(XMIN, 0.0)
        VALUE_MAP.put(XMAX, 0.0)
        VALUE_MAP.put(XEND, 0.0)
        VALUE_MAP.put(YEND, 0.0)
        VALUE_MAP.put(LABEL, "-")
        VALUE_MAP.put(FAMILY, "sans-serif")
        VALUE_MAP.put(FONTFACE, "plain")
        VALUE_MAP.put(LINEHEIGHT, 1.0)
        VALUE_MAP.put(HJUST, 0.5)  // 'middle'
        VALUE_MAP.put(VJUST, 0.5)  // 'middle'
        VALUE_MAP.put(ANGLE, 0.0)
        VALUE_MAP.put(SLICE, 0.0)
        VALUE_MAP.put(EXPLODE, 0.0)
    }

    /**
     * For test only (must be TRUE for any Aes)
     */
    fun has(aes: Aes<*>): Boolean {
        return VALUE_MAP.containsKey(aes)
    }

    operator fun <T> get(aes: Aes<T>): T {
        return VALUE_MAP[aes]
    }

    operator fun <T> get(aes: TypedKey<T>): T {
        return VALUE_MAP[aes]
    }
}
