/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.aes

import org.jetbrains.letsPlot.core.commons.typedKey.TypedKey
import org.jetbrains.letsPlot.core.commons.typedKey.TypedKeyHashMap
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.ALPHA
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.ANGLE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.BINWIDTH
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.COLOR
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.EXPLODE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FAMILY
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FILL
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FLOW
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FONTFACE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FRAME
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.HEIGHT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.HJUST
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.INTERCEPT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LABEL
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LINEHEIGHT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LINETYPE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LOWER
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.MAP_ID
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.MIDDLE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.PAINT_A
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.PAINT_B
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.PAINT_C
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SAMPLE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.QUANTILE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SHAPE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SIZE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.STROKE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LINEWIDTH
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SLICE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SLOPE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SPEED
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.STACKSIZE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.UPPER
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.VIOLINWIDTH
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.VJUST
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.WEIGHT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.WIDTH
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.X
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XEND
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XINTERCEPT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XMAX
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XMIN
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.Y
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YEND
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YINTERCEPT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YMAX
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YMIN
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.Z
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape

object AesInitValue {

    const val DEFAULT_ALPHA = 0.999887

    private val VALUE_MAP = TypedKeyHashMap()

    init {
        VALUE_MAP[X] = 0.0
        VALUE_MAP[Y] = 0.0
        VALUE_MAP[Z] = 0.0
        VALUE_MAP[YMIN] = Double.NaN
        VALUE_MAP[YMAX] = Double.NaN
        VALUE_MAP[COLOR] = Color.PACIFIC_BLUE
        VALUE_MAP[FILL] = Color.PACIFIC_BLUE
        VALUE_MAP[PAINT_A] = Color.PACIFIC_BLUE
        VALUE_MAP[PAINT_B] = Color.PACIFIC_BLUE
        VALUE_MAP[PAINT_C] = Color.PACIFIC_BLUE
        VALUE_MAP[ALPHA] = DEFAULT_ALPHA
        VALUE_MAP[SHAPE] = NamedShape.SOLID_CIRCLE
        VALUE_MAP[LINETYPE] = NamedLineType.SOLID
        VALUE_MAP[SIZE] = 0.5 // Line thickness. Should be redefined for other shapes
        VALUE_MAP[STROKE] = 0.5 // Point border thickness
        VALUE_MAP[LINEWIDTH] = 0.5 // Line thickness for lollipop. Should be used for other line geoms.
        VALUE_MAP[STACKSIZE] = 0.0
        VALUE_MAP[WIDTH] = 1.0
        VALUE_MAP[HEIGHT] = 1.0
        VALUE_MAP[BINWIDTH] = 1.0
        VALUE_MAP[VIOLINWIDTH] = 0.0
        VALUE_MAP[WEIGHT] = 1.0
        VALUE_MAP[INTERCEPT] = 0.0
        VALUE_MAP[SLOPE] = 1.0
        VALUE_MAP[XINTERCEPT] = 0.0
        VALUE_MAP[YINTERCEPT] = 0.0
        VALUE_MAP[LOWER] = Double.NaN
        VALUE_MAP[MIDDLE] = Double.NaN
        VALUE_MAP[UPPER] = Double.NaN
        VALUE_MAP[SAMPLE] = 0.0
        VALUE_MAP[QUANTILE] = Double.NaN
        VALUE_MAP[MAP_ID] = "empty map_id"
        VALUE_MAP[FRAME] = "empty frame"
        VALUE_MAP[SPEED] = 10.0
        VALUE_MAP[FLOW] = 0.1
        VALUE_MAP[XMIN] = Double.NaN
        VALUE_MAP[XMAX] = Double.NaN
        VALUE_MAP[XEND] = Double.NaN
        VALUE_MAP[YEND] = Double.NaN
        VALUE_MAP[LABEL] = ""
        VALUE_MAP[FAMILY] = "sans-serif"
        VALUE_MAP[FONTFACE] = "plain"
        VALUE_MAP[LINEHEIGHT] = 1.0
        VALUE_MAP[HJUST] = 0.5  // 'middle'
        VALUE_MAP[VJUST] = 0.5  // 'middle'
        VALUE_MAP[ANGLE] = 0.0
        VALUE_MAP[SLICE] = 0.0
        VALUE_MAP[EXPLODE] = 0.0
    }

    /**
     * For test only (must br TRUE for any Aes)
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
