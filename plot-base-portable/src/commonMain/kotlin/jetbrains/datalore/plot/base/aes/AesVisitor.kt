/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.aes

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
import jetbrains.datalore.plot.base.Aes.Companion.QUANTILE
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
        if (aes == ALPHA) {
            return alpha()
        }
        if (aes == SHAPE) {
            return shape()
        }
        if (aes == SIZE) {
            return size()
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

        if (aes == SYM_X) {
            return symX()
        }

        if (aes == SYM_Y) {
            return symY()
        }

        if (aes == SLICE) {
            return slice()
        }

        if (aes == EXPLODE) {
            return explode()
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

    protected abstract fun alpha(): T

    protected abstract fun shape(): T

    protected abstract fun lineType(): T

    protected abstract fun size(): T

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

    protected abstract fun symX(): T

    protected abstract fun symY(): T

    protected abstract fun slice(): T

    protected abstract fun explode(): T
}
