/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShape

abstract class DataPointAesthetics {
    abstract fun index(): Int

    abstract fun group(): Int?

    abstract operator fun <T> get(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>): T?

    abstract val colorAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color>

    abstract val fillAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color>

    fun x(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.X)
    }

    fun y(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.Y)
    }

    fun z(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.Z)
    }

    fun ymin(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.YMIN)
    }

    fun ymax(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.YMAX)
    }

    fun color(): Color? {
        return get(colorAes)
    }

    fun fill(): Color? {
        return get(fillAes)
    }

    fun alpha(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA)
    }

    fun shape(): PointShape? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.SHAPE)
    }

    fun lineType(): LineType {
        return getNotNull(org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE)
    }

    fun size(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.SIZE)
    }

    fun stroke(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.STROKE)
    }

    fun linewidth(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.LINEWIDTH)
    }

    fun stacksize(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.STACKSIZE)
    }

    fun width(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.WIDTH)
    }

    fun height(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.HEIGHT)
    }

    fun binwidth(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.BINWIDTH)
    }

    fun violinwidth(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.VIOLINWIDTH)
    }

    fun weight(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.WEIGHT)
    }

    fun intercept(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.INTERCEPT)
    }

    fun slope(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.SLOPE)
    }

    fun interceptX(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.XINTERCEPT)
    }

    fun interceptY(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.YINTERCEPT)
    }

    fun lower(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.LOWER)
    }

    fun middle(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.MIDDLE)
    }

    fun upper(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.UPPER)
    }

    fun sample(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.SAMPLE)
    }

    fun quantile(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.QUANTILE)
    }

    fun mapId(): Any {
        return getNotNull(org.jetbrains.letsPlot.core.plot.base.Aes.MAP_ID)
    }

    fun frame(): String {
        return getNotNull(org.jetbrains.letsPlot.core.plot.base.Aes.FRAME)
    }

    fun speed(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.SPEED)
    }

    fun flow(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.FLOW)
    }

    fun xmin(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.XMIN)
    }

    fun xmax(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.XMAX)
    }

    fun xend(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.XEND)
    }

    fun yend(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.YEND)
    }

    fun label(): Any? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.LABEL)
    }

    fun family(): String {
        return getNotNull(org.jetbrains.letsPlot.core.plot.base.Aes.FAMILY)
    }

    fun fontface(): String {
        return getNotNull(org.jetbrains.letsPlot.core.plot.base.Aes.FONTFACE)
    }

    fun lineheight(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.LINEHEIGHT)
    }

    fun hjust(): Any {
        return getNotNull(org.jetbrains.letsPlot.core.plot.base.Aes.HJUST)
    }

    fun vjust(): Any {
        return getNotNull(org.jetbrains.letsPlot.core.plot.base.Aes.VJUST)
    }

    fun angle(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.ANGLE)
    }

    fun slice(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.SLICE)
    }

    fun explode(): Double? {
        return get(org.jetbrains.letsPlot.core.plot.base.Aes.EXPLODE)
    }

    fun numeric(aes: org.jetbrains.letsPlot.core.plot.base.Aes<Double>): Double? {
        return get(aes)
    }

    fun defined(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
        if (aes.isNumeric) {
            val number = get(aes)
            return number != null && (number as Double).isFinite()
        }
        return true
    }

    private fun <T> getNotNull(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>): T {
        return get(aes) ?: throw IllegalStateException("No value for aesthetic $aes at index ${index()}")
    }
}
