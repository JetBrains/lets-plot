/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.aes

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aes.Companion.ALPHA
import jetbrains.datalore.plot.base.Aes.Companion.ANGLE
import jetbrains.datalore.plot.base.Aes.Companion.BINWIDTH
import jetbrains.datalore.plot.base.Aes.Companion.COLOR
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

internal class TypedOptionConverterMap {

    private val myMap = HashMap<Aes<*>, (Any?) -> Any?>()

    init {
        this.put(X, DOUBLE_CVT)
        this.put(Y, DOUBLE_CVT)

        this.put(Z, DOUBLE_CVT)
        this.put(YMIN, DOUBLE_CVT)
        this.put(YMAX, DOUBLE_CVT)
        this.put(COLOR, COLOR_CVT)
        this.put(FILL, COLOR_CVT)
        this.put(ALPHA, DOUBLE_CVT)
        this.put(SHAPE, SHAPE_CVT)
        this.put(LINETYPE, LINETYPE_CVT)

        this.put(SIZE, DOUBLE_CVT)
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
        this.put(SAMPLE, DOUBLE_CVT)

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
        this.put(HJUST, IDENTITY_O_CVT)   // text horizontal justification (numbers [0..1] or predefined strings, DOUBLE_CVT; not positional)
        this.put(VJUST, IDENTITY_O_CVT)   // text vertical justification (numbers [0..1] or predefined strings, not positional)
        this.put(ANGLE, DOUBLE_CVT)
        this.put(SYM_X, DOUBLE_CVT)
        this.put(SYM_Y, DOUBLE_CVT)
        this.put(SLICE, DOUBLE_CVT)
    }

    private fun <T> put(aes: Aes<T>, value: (Any?) -> T?) {
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
        private val COLOR_CVT = { o: Any? -> ColorOptionConverter().apply(o) }
        private val SHAPE_CVT = { o: Any? -> ShapeOptionConverter().apply(o) }
        private val LINETYPE_CVT = { o: Any? -> LineTypeOptionConverter().apply(o) }
    }
}
