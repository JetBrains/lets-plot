/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.linetype.LineType
import jetbrains.datalore.plot.base.render.point.PointShape

abstract class DataPointAesthetics {
    abstract fun index(): Int

    abstract fun group(): Int?

    abstract operator fun <T> get(aes: Aes<T>): T?

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
        return get(Aes.COLOR)
    }

    fun fill(): Color? {
        return get(Aes.FILL)
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

    fun symX(): Double? {
        return get(Aes.SYM_X)
    }

    fun symY(): Double? {
        return get(Aes.SYM_Y)
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

    private fun <T> getNotNull(aes: Aes<T>): T {
        return get(aes) ?: throw IllegalStateException("No value for aesthetic $aes at index ${index()}")
    }
}
