/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.render.linetype.LineType
import jetbrains.datalore.plot.base.render.point.PointShape

open class DataPointAestheticsDelegate(
    private val p: DataPointAesthetics
) : DataPointAesthetics {

    final override fun index(): Int {
        return p.index()
    }

    final override fun x(): Double? {
        return get(Aes.X)
    }

    final override fun y(): Double? {
        return get(Aes.Y)
    }

    final override fun z(): Double? {
        return get(Aes.Z)
    }

    final override fun ymin(): Double? {
        return get(Aes.YMIN)
    }

    final override fun ymax(): Double? {
        return get(Aes.YMAX)
    }

    final override fun color(): Color? {
        return get(Aes.COLOR)
    }

    final override fun fill(): Color? {
        return get(Aes.FILL)
    }

    final override fun alpha(): Double? {
        return get(Aes.ALPHA)
    }

    final override fun shape(): PointShape? {
        return get(Aes.SHAPE)
    }

    final override fun lineType(): LineType {
        return get(Aes.LINETYPE)!!
    }

    final override fun size(): Double? {
        return get(Aes.SIZE)
    }

    final override fun stacksize(): Double? {
        return get(Aes.STACKSIZE)
    }

    final override fun width(): Double? {
        return get(Aes.WIDTH)
    }

    final override fun height(): Double? {
        return get(Aes.HEIGHT)
    }

    final override fun binwidth(): Double? {
        return get(Aes.BINWIDTH)
    }

    final override fun violinwidth(): Double? {
        return get(Aes.VIOLINWIDTH)
    }

    final override fun weight(): Double? {
        return get(Aes.WEIGHT)
    }

    final override fun intercept(): Double? {
        return get(Aes.INTERCEPT)
    }

    final override fun slope(): Double? {
        return get(Aes.SLOPE)
    }

    final override fun interceptX(): Double? {
        return get(Aes.XINTERCEPT)
    }

    final override fun interceptY(): Double? {
        return get(Aes.YINTERCEPT)
    }

    final override fun lower(): Double? {
        return get(Aes.LOWER)
    }

    final override fun middle(): Double? {
        return get(Aes.MIDDLE)
    }

    final override fun upper(): Double? {
        return get(Aes.UPPER)
    }

    final override fun mapId(): Any {
        return get(Aes.MAP_ID)!!
    }

    final override fun frame(): String {
        return get(Aes.FRAME)!!
    }

    final override fun speed(): Double? {
        return get(Aes.SPEED)
    }

    final override fun flow(): Double? {
        return get(Aes.FLOW)
    }

    final override fun xmin(): Double? {
        return get(Aes.XMIN)
    }

    final override fun xmax(): Double? {
        return get(Aes.XMAX)
    }

    final override fun xend(): Double? {
        return get(Aes.XEND)
    }

    final override fun yend(): Double? {
        return get(Aes.YEND)
    }

    final override fun label(): Any? {
        return get(Aes.LABEL)
    }

    final override fun family(): String {
        return get(Aes.FAMILY)!!
    }

    final override fun fontface(): String {
        return get(Aes.FONTFACE)!!
    }

    final override fun hjust(): Any {
        return get(Aes.HJUST)!!
    }

    final override fun vjust(): Any {
        return get(Aes.VJUST)!!
    }

    final override fun angle(): Double? {
        return get(Aes.ANGLE)
    }

    final override fun symX(): Double? {
        return get(Aes.SYM_X)
    }

    final override fun symY(): Double? {
        return get(Aes.SYM_Y)
    }

    final override fun group(): Int? {
        return p.group()
    }

    final override fun numeric(aes: Aes<Double>): Double? {
        return get(aes)
    }

    override fun <T> get(aes: Aes<T>): T? {
        return p.get(aes)
    }
}
