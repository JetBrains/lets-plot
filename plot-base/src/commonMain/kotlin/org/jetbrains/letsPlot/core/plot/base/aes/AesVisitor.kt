/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.aes

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
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.RADIUS
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SHAPE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SIZE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.STROKE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LINEWIDTH
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SLICE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SLOPE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SPEED
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.STACKSIZE
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
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XMAX
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XMIN
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.Y
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YEND
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YINTERCEPT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YMAX
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YMIN
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.Z

abstract class AesVisitor<T> {
    fun visit(aes: Aes<*>): T {
        return if (aes.isNumeric) {
            // Safe cast because all 'numeric' aesthetics are <Double>
            @Suppress("UNCHECKED_CAST")
            visitNumeric(aes as Aes<Double>)
        } else visitIntern(aes)
    }

    /**
     * Descendants can override
     *
     * @param aes
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun visitNumeric(aes: Aes<Double>): T {
        return visitIntern(aes)
    }

    private fun visitIntern(aes: Aes<*>): T {
        if (aes == X) {
            return x()
        }
        if (aes == Y) {
            return y()
        }
        if (aes == Z) {
            return z()
        }
        if (aes == YMIN) {
            return ymin()
        }
        if (aes == YMAX) {
            return ymax()
        }
        if (aes == COLOR) {
            return color()
        }
        if (aes == FILL) {
            return fill()
        }
        if (aes == PAINT_A) {
            return paint_a()
        }
        if (aes == PAINT_B) {
            return paint_b()
        }
        if (aes == PAINT_C) {
            return paint_c()
        }
        if (aes == ALPHA) {
            return alpha()
        }
        if (aes == SHAPE) {
            return shape()
        }
        if (aes == SIZE) {
            return size()
        }
        if (aes == STROKE) {
            return stroke()
        }
        if (aes == LINEWIDTH) {
            return linewidth()
        }
        if (aes == STACKSIZE) {
            return stacksize()
        }
        if (aes == LINETYPE) {
            return lineType()
        }
        if (aes == WIDTH) {
            return width()
        }
        if (aes == HEIGHT) {
            return height()
        }
        if (aes == BINWIDTH) {
            return binwidth()
        }
        if (aes == VIOLINWIDTH) {
            return violinwidth()
        }
        if (aes == WEIGHT) {
            return weight()
        }
        if (aes == INTERCEPT) {
            return intercept()
        }
        if (aes == SLOPE) {
            return slope()
        }
        if (aes == XINTERCEPT) {
            return interceptX()
        }
        if (aes == YINTERCEPT) {
            return interceptY()
        }
        if (aes == LOWER) {
            return lower()
        }
        if (aes == MIDDLE) {
            return middle()
        }
        if (aes == UPPER) {
            return upper()
        }
        if (aes == SAMPLE) {
            return sample()
        }
        if (aes == QUANTILE) {
            return quantile()
        }
        if (aes == MAP_ID) {
            return mapId()
        }
        if (aes == FRAME) {
            return frame()
        }
        if (aes == SPEED) {
            return speed()
        }
        if (aes == FLOW) {
            return flow()
        }
        if (aes == XMIN) {
            return xmin()
        }
        if (aes == XMAX) {
            return xmax()
        }
        if (aes == XEND) {
            return xend()
        }
        if (aes == YEND) {
            return yend()
        }
        if (aes == LABEL) {
            return label()
        }
        if (aes == FAMILY) {
            return family()
        }
        if (aes == FONTFACE) {
            return fontface()
        }
        if (aes == LINEHEIGHT) {
            return lineheight()
        }
        if (aes == HJUST) {
            return hjust()
        }
        if (aes == VJUST) {
            return vjust()
        }
        if (aes == ANGLE) {
            return angle()
        }
        if (aes == RADIUS) {
            return radius()
        }
        if (aes == SLICE) {
            return slice()
        }
        if (aes == EXPLODE) {
            return explode()
        }
        if (aes == SIZE_START) {
            return sizeStart()
        }
        if (aes == SIZE_END) {
            return sizeEnd()
        }
        if (aes == STROKE_START) {
            return strokeStart()
        }
        if (aes == STROKE_END) {
            return strokeEnd()
        }

        throw IllegalArgumentException("Unexpected aes: $aes")
    }

    protected abstract fun x(): T

    protected abstract fun y(): T

    protected abstract fun z(): T

    protected abstract fun ymin(): T

    protected abstract fun ymax(): T

    protected abstract fun color(): T

    protected abstract fun fill(): T

    protected abstract fun paint_a(): T

    protected abstract fun paint_b(): T

    protected abstract fun paint_c(): T

    protected abstract fun alpha(): T

    protected abstract fun shape(): T

    protected abstract fun lineType(): T

    protected abstract fun size(): T

    protected abstract fun stroke(): T

    protected abstract fun linewidth(): T

    protected abstract fun stacksize(): T

    protected abstract fun width(): T

    protected abstract fun height(): T

    protected abstract fun binwidth(): T

    protected abstract fun violinwidth(): T

    protected abstract fun weight(): T

    protected abstract fun intercept(): T

    protected abstract fun slope(): T

    protected abstract fun interceptX(): T

    protected abstract fun interceptY(): T

    protected abstract fun lower(): T

    protected abstract fun middle(): T

    protected abstract fun upper(): T

    protected abstract fun sample(): T

    protected abstract fun quantile(): T

    protected abstract fun mapId(): T

    protected abstract fun frame(): T

    protected abstract fun speed(): T

    protected abstract fun flow(): T

    protected abstract fun xmin(): T

    protected abstract fun xmax(): T

    protected abstract fun xend(): T

    protected abstract fun yend(): T

    protected abstract fun label(): T

    protected abstract fun family(): T

    protected abstract fun fontface(): T

    protected abstract fun lineheight(): T

    protected abstract fun hjust(): T

    protected abstract fun vjust(): T

    protected abstract fun angle(): T

    protected abstract fun radius(): T

    protected abstract fun slice(): T

    protected abstract fun explode(): T

    protected abstract fun sizeStart(): T

    protected abstract fun sizeEnd(): T

    protected abstract fun strokeStart(): T

    protected abstract fun strokeEnd(): T
}
