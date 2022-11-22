/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.aes

import jetbrains.datalore.base.typedKey.TypedKey
import jetbrains.datalore.base.typedKey.TypedKeyHashMap
import jetbrains.datalore.base.values.Color
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
import jetbrains.datalore.plot.base.Aes.Companion.SAMPLE
import jetbrains.datalore.plot.base.Aes.Companion.SHAPE
import jetbrains.datalore.plot.base.Aes.Companion.SIZE
import jetbrains.datalore.plot.base.Aes.Companion.SLICE
import jetbrains.datalore.plot.base.Aes.Companion.SLOPE
import jetbrains.datalore.plot.base.Aes.Companion.SPEED
import jetbrains.datalore.plot.base.Aes.Companion.STACKSIZE
import jetbrains.datalore.plot.base.Aes.Companion.SYM_X
import jetbrains.datalore.plot.base.Aes.Companion.SYM_Y
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
import jetbrains.datalore.plot.base.render.linetype.NamedLineType
import jetbrains.datalore.plot.base.render.point.NamedShape

object AesInitValue {

    private val VALUE_MAP = TypedKeyHashMap()

    init {
        VALUE_MAP[X] = 0.0
        VALUE_MAP[Y] = 0.0
        VALUE_MAP[Z] = 0.0
        VALUE_MAP[YMIN] = Double.NaN
        VALUE_MAP[YMAX] = Double.NaN
        VALUE_MAP[COLOR] = Color.PACIFIC_BLUE
        VALUE_MAP[FILL] = Color.PACIFIC_BLUE
        VALUE_MAP[ALPHA] = 1.0
        VALUE_MAP[SHAPE] = NamedShape.SOLID_CIRCLE
        VALUE_MAP[LINETYPE] = NamedLineType.SOLID
        VALUE_MAP[SIZE] = 0.5  // Line thickness. Should be redefined for other shapes
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
        VALUE_MAP[SYM_X] = 0.0
        VALUE_MAP[SYM_Y] = 0.0
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
        return VALUE_MAP.get<T>(aes)
    }

    operator fun <T> get(aes: TypedKey<T>): T {
        return VALUE_MAP.get<T>(aes)
    }
}
