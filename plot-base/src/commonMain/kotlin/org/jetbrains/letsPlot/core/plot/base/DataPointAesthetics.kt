/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShape

abstract class DataPointAesthetics {
    abstract fun index(): Int

    abstract fun group(): Int?

    abstract operator fun <T> get(aes: Aes<T>): T?

    abstract val colorAes: Aes<Color>

    abstract val fillAes: Aes<Color>

    fun x(): Double? {
        return get(Aes.X)
    }

    fun y(): Double? {
        return get(Aes.Y)
    }

    fun z(): Double? {
        return get(Aes.Z)
    }

    fun ymin(): Double? {
        return get(Aes.YMIN)
    }

    fun ymax(): Double? {
        return get(Aes.YMAX)
    }

    fun color(): Color? {
        return get(colorAes)
    }

    fun fill(): Color? {
        return get(fillAes)
    }

    fun alpha(): Double? {
        return get(Aes.ALPHA)
    }

    fun shape(): PointShape? {
        return get(Aes.SHAPE)
    }

    fun lineType(): LineType {
        return getNotNull(Aes.LINETYPE)
    }

    fun size(): Double? {
        return get(Aes.SIZE)
    }

    fun stroke(): Double? {
        return get(Aes.STROKE)
    }

    fun linewidth(): Double? {
        return get(Aes.LINEWIDTH)
    }

    fun stacksize(): Double? {
        return get(Aes.STACKSIZE)
    }

    fun width(): Double? {
        return get(Aes.WIDTH)
    }

    fun height(): Double? {
        return get(Aes.HEIGHT)
    }

    fun binwidth(): Double? {
        return get(Aes.BINWIDTH)
    }

    fun violinwidth(): Double? {
        return get(Aes.VIOLINWIDTH)
    }

    fun weight(): Double? {
        return get(Aes.WEIGHT)
    }

    fun intercept(): Double? {
        return get(Aes.INTERCEPT)
    }

    fun slope(): Double? {
        return get(Aes.SLOPE)
    }

    fun interceptX(): Double? {
        return get(Aes.XINTERCEPT)
    }

    fun interceptY(): Double? {
        return get(Aes.YINTERCEPT)
    }

    fun lower(): Double? {
        return get(Aes.LOWER)
    }

    fun middle(): Double? {
        return get(Aes.MIDDLE)
    }

    fun upper(): Double? {
        return get(Aes.UPPER)
    }

    fun sample(): Double? {
        return get(Aes.SAMPLE)
    }

    fun quantile(): Double? {
        return get(Aes.QUANTILE)
    }

    fun mapId(): Any {
        return getNotNull(Aes.MAP_ID)
    }

    fun frame(): String {
        return getNotNull(Aes.FRAME)
    }

    fun speed(): Double? {
        return get(Aes.SPEED)
    }

    fun flow(): Double? {
        return get(Aes.FLOW)
    }

    fun xmin(): Double? {
        return get(Aes.XMIN)
    }

    fun xmax(): Double? {
        return get(Aes.XMAX)
    }

    fun xend(): Double? {
        return get(Aes.XEND)
    }

    fun yend(): Double? {
        return get(Aes.YEND)
    }

    fun label(): Any? {
        return get(Aes.LABEL)
    }

    fun family(): String {
        return getNotNull(Aes.FAMILY)
    }

    fun fontface(): String {
        return getNotNull(Aes.FONTFACE)
    }

    fun lineheight(): Double? {
        return get(Aes.LINEHEIGHT)
    }

    fun hjust(): Any {
        return getNotNull(Aes.HJUST)
    }

    fun vjust(): Any {
        return getNotNull(Aes.VJUST)
    }

    fun angle(): Double? {
        return get(Aes.ANGLE)
    }

    fun radius(): Double? {
        return get(Aes.RADIUS)
    }

    fun slice(): Double? {
        return get(Aes.SLICE)
    }

    fun explode(): Double? {
        return get(Aes.EXPLODE)
    }

    fun sizeStart(): Double? {
        return get(Aes.SIZE_START)
    }

    fun sizeEnd(): Double? {
        return get(Aes.SIZE_END)
    }

    fun strokeStart(): Double? {
        return get(Aes.STROKE_START)
    }

    fun strokeEnd(): Double? {
        return get(Aes.STROKE_END)
    }

    fun numeric(aes: Aes<Double>): Double? {
        return get(aes)
    }

    fun defined(aes: Aes<*>): Boolean {
        if (aes.isNumeric) {
            val number = get(aes)
            return number != null && (number as Double).isFinite()
        }
        return true
    }

    fun finiteOrNull(aes: Aes<Double>): Double? {
        if (!defined(aes)) return null
        return get(aes)
    }

    fun finiteOrNull(aes0: Aes<Double>, aes1: Aes<Double>): Pair<Double, Double>? {
        val v0 = finiteOrNull(aes0) ?: return null
        val v1 = finiteOrNull(aes1) ?: return null
        return Pair(v0, v1)
    }

    fun finiteOrNull(aes0: Aes<Double>, aes1: Aes<Double>, aes2: Aes<Double>): Triple<Double, Double, Double>? {
        val v0 = finiteOrNull(aes0) ?: return null
        val v1 = finiteOrNull(aes1) ?: return null
        val v2 = finiteOrNull(aes2) ?: return null
        return Triple(v0, v1, v2)
    }

    fun doubleVectorOrNull(aes0: Aes<Double>, aes1: Aes<Double>): DoubleVector? {
        val (x, y) = finiteOrNull(aes0, aes1) ?: return null
        return DoubleVector(x, y)
    }

    private fun <T> getNotNull(aes: Aes<T>): T {
        return get(aes) ?: throw IllegalStateException("No value for aesthetic $aes at index ${index()}")
    }
}
