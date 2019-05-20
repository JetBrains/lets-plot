package jetbrains.datalore.visualization.plot.base.render

import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.ALPHA
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.ANGLE
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.COLOR
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.FAMILY
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.FILL
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.FLOW
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.FONTFACE
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.FRAME
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.HEIGHT
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.HJUST
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.INTERCEPT
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.LABEL
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.LINETYPE
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.LOWER
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.MAP_ID
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.MIDDLE
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.SHAPE
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.SIZE
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.SLOPE
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.SPEED
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.UPPER
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.VJUST
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.WEIGHT
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.WIDTH
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.X
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.XEND
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.XINTERCEPT
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.XMAX
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.XMIN
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.Y
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.YEND
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.YINTERCEPT
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.YMAX
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.YMIN
import jetbrains.datalore.visualization.plot.base.render.Aes.Companion.Z

abstract class AesVisitor<T> {
    fun visit(aes: Aes<*>): T {
        return if (aes.isNumeric) {
            // Safe cast because all 'numeric' aesthetics are <Double>
            visitNumeric(aes as Aes<Double>)
        } else visitIntern(aes)
    }

    /**
     * Descendants can override
     *
     * @param aes
     */
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
        if (aes == LINETYPE) {
            return lineType()
        }
        if (aes == WIDTH) {
            return width()
        }
        if (aes == HEIGHT) {
            return height()
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
        if (aes == HJUST) {
            return hjust()
        }
        if (aes == VJUST) {
            return vjust()
        }
        if (aes == ANGLE) {
            return angle()
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

    protected abstract fun width(): T

    protected abstract fun height(): T

    protected abstract fun weight(): T

    protected abstract fun intercept(): T

    protected abstract fun slope(): T

    protected abstract fun interceptX(): T

    protected abstract fun interceptY(): T

    protected abstract fun lower(): T

    protected abstract fun middle(): T

    protected abstract fun upper(): T

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

    protected abstract fun hjust(): T

    protected abstract fun vjust(): T

    protected abstract fun angle(): T
}
