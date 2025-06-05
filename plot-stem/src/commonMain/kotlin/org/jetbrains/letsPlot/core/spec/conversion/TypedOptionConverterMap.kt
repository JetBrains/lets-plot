/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.conversion

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
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LINEWIDTH
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LOWER
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.MAP_ID
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.MIDDLE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.PAINT_A
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.PAINT_B
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.PAINT_C
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.POINT_SIZE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.QUANTILE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.RADIUS
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SAMPLE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SEGMENT_ALPHA
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SEGMENT_COLOR
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SEGMENT_SIZE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SHAPE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SIZE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SLICE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SLOPE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SPEED
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.STACKSIZE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.STROKE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SIZE_START
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SIZE_END
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.STROKE_END
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.STROKE_START
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.UPPER
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.VIOLINWIDTH
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.VJUST
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.WEIGHT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.WIDTH
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.X
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XEND
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XINTERCEPT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XLOWER
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XMAX
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XMIDDLE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XMIN
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XUPPER
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.Y
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YEND
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YINTERCEPT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YMAX
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YMIN
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.Z

internal class TypedOptionConverterMap(colorConverter: ColorOptionConverter) {

    private val colorCvt = { o: Any? -> colorConverter.apply(o) }

    private val myMap = HashMap<Aes<*>, (Any?) -> Any?>()

    init {
        this.put(X, DOUBLE_CVT)
        this.put(Y, DOUBLE_CVT)

        this.put(Z, DOUBLE_CVT)
        this.put(YMIN, DOUBLE_CVT)
        this.put(YMAX, DOUBLE_CVT)
        this.put(COLOR, colorCvt)
        this.put(FILL, colorCvt)
        this.put(PAINT_A, colorCvt)
        this.put(PAINT_B, colorCvt)
        this.put(PAINT_C, colorCvt)
        this.put(ALPHA, DOUBLE_CVT)
        this.put(SHAPE, SHAPE_CVT)
        this.put(LINETYPE, LINETYPE_CVT)

        this.put(SIZE, DOUBLE_CVT)
        this.put(STROKE, DOUBLE_CVT)
        this.put(LINEWIDTH, DOUBLE_CVT)
        this.put(STACKSIZE, DOUBLE_CVT)
        this.put(WIDTH, DOUBLE_CVT)
        this.put(HEIGHT, DOUBLE_CVT)
        this.put(BINWIDTH, DOUBLE_CVT)
        this.put(VIOLINWIDTH, DOUBLE_CVT)
        this.put(WEIGHT, DOUBLE_CVT)
        this.put(INTERCEPT, DOUBLE_CVT)
        this.put(SLOPE, DOUBLE_CVT)
        this.put(XINTERCEPT, DOUBLE_CVT)
        this.put(YINTERCEPT, DOUBLE_CVT)
        this.put(LOWER, DOUBLE_CVT)
        this.put(MIDDLE, DOUBLE_CVT)
        this.put(UPPER, DOUBLE_CVT)
        this.put(XLOWER, DOUBLE_CVT)
        this.put(XMIDDLE, DOUBLE_CVT)
        this.put(XUPPER, DOUBLE_CVT)
        this.put(SAMPLE, DOUBLE_CVT)
        this.put(QUANTILE, DOUBLE_CVT)

        this.put(MAP_ID, IDENTITY_O_CVT)
        this.put(FRAME, IDENTITY_S_CVT)

        this.put(SPEED, DOUBLE_CVT)
        this.put(FLOW, DOUBLE_CVT)

        this.put(XMIN, DOUBLE_CVT)
        this.put(XMAX, DOUBLE_CVT)
        this.put(XEND, DOUBLE_CVT)
        this.put(YEND, DOUBLE_CVT)

        this.put(LABEL, IDENTITY_O_CVT)
        this.put(FAMILY, IDENTITY_S_CVT)
        this.put(FONTFACE, IDENTITY_S_CVT)
        this.put(LINEHEIGHT, DOUBLE_CVT)
        this.put(
            HJUST,
            IDENTITY_O_CVT
        )   // text horizontal justification (numbers [0..1] or predefined strings, DOUBLE_CVT; not positional)
        this.put(
            VJUST,
            IDENTITY_O_CVT
        )   // text vertical justification (numbers [0..1] or predefined strings, not positional)
        this.put(ANGLE, DOUBLE_CVT)
        this.put(RADIUS, DOUBLE_CVT)
        this.put(SLICE, DOUBLE_CVT)
        this.put(EXPLODE, DOUBLE_CVT)

        this.put(SIZE_START, DOUBLE_CVT)
        this.put(SIZE_END, DOUBLE_CVT)
        this.put(STROKE_START, DOUBLE_CVT)
        this.put(STROKE_END, DOUBLE_CVT)

        this.put(POINT_SIZE, DOUBLE_CVT)
        this.put(SEGMENT_COLOR, colorCvt)
        this.put(SEGMENT_SIZE, DOUBLE_CVT)
        this.put(SEGMENT_ALPHA, DOUBLE_CVT)
    }

    internal fun <T> put(aes: Aes<T>, value: (Any?) -> T?) {
        myMap[aes] = value
    }

    operator fun <T> get(aes: Aes<T>): (Any?) -> T? {
        // Safe cast because 'put' is private
        @Suppress("UNCHECKED_CAST")
        return myMap[aes] as (Any?) -> T?
    }

    fun containsKey(aes: Aes<*>): Boolean {
        return myMap.containsKey(aes)
    }

    companion object {
        private val IDENTITY_O_CVT = { o: Any? -> o }
        private val IDENTITY_S_CVT = { o: Any? -> o?.toString() }
        private val DOUBLE_CVT = { o: Any? -> NumericOptionConverter().apply(o) }
        private val SHAPE_CVT = { o: Any? -> ShapeOptionConverter().apply(o) }
        private val LINETYPE_CVT = { o: Any? -> LineTypeOptionConverter().apply(o) }
    }
}
