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

    override fun index(): Int {
        return p.index()
    }

    override fun x(): Double? {
        return get(Aes.X)
    }

    override fun y(): Double? {
        return get(Aes.Y)
    }

    override fun z(): Double? {
        return get(Aes.Z)
    }

    override fun ymin(): Double? {
        return get(Aes.YMIN)
    }

    override fun ymax(): Double? {
        return get(Aes.YMAX)
    }

    override fun color(): Color? {
        return get(Aes.COLOR)
    }

    override fun fill(): Color? {
        return get(Aes.FILL)
    }

    override fun alpha(): Double? {
        return get(Aes.ALPHA)
    }

    override fun shape(): PointShape? {
        return get(Aes.SHAPE)
    }

    override fun lineType(): LineType {
        return get(Aes.LINETYPE)!!
    }

    override fun size(): Double? {
        return get(Aes.SIZE)
    }

    override fun stacksize(): Double? {
        return get(Aes.STACKSIZE)
    }

    override fun width(): Double? {
        return get(Aes.WIDTH)
    }

    override fun height(): Double? {
        return get(Aes.HEIGHT)
    }

    override fun binwidth(): Double? {
        return get(Aes.BINWIDTH)
    }

    override fun violinwidth(): Double? {
        return get(Aes.VIOLINWIDTH)
    }

    override fun weight(): Double? {
        return get(Aes.WEIGHT)
    }

    override fun intercept(): Double? {
        return get(Aes.INTERCEPT)
    }

    override fun slope(): Double? {
        return get(Aes.SLOPE)
    }

    override fun interceptX(): Double? {
        return get(Aes.XINTERCEPT)
    }

    override fun interceptY(): Double? {
        return get(Aes.YINTERCEPT)
    }

    override fun lower(): Double? {
        return get(Aes.LOWER)
    }

    override fun middle(): Double? {
        return get(Aes.MIDDLE)
    }

    override fun upper(): Double? {
        return get(Aes.UPPER)
    }

    override fun mapId(): Any {
        return get(Aes.MAP_ID)!!
    }

    override fun frame(): String {
        return get(Aes.FRAME)!!
    }

    override fun speed(): Double? {
        return get(Aes.SPEED)
    }

    override fun flow(): Double? {
        return get(Aes.FLOW)
    }

    override fun xmin(): Double? {
        return get(Aes.XMIN)
    }

    override fun xmax(): Double? {
        return get(Aes.XMAX)
    }

    override fun xend(): Double? {
        return get(Aes.XEND)
    }

    override fun yend(): Double? {
        return get(Aes.YEND)
    }

    override fun label(): Any? {
        return get(Aes.LABEL)
    }

    override fun family(): String {
        return get(Aes.FAMILY)!!
    }

    override fun fontface(): String {
        return get(Aes.FONTFACE)!!
    }

    override fun hjust(): Any {
        return get(Aes.HJUST)!!
    }

    override fun vjust(): Any {
        return get(Aes.VJUST)!!
    }

    override fun angle(): Double? {
        return get(Aes.ANGLE)
    }

    override fun symX(): Double? {
        return get(Aes.SYM_X)
    }

    override fun symY(): Double? {
        return get(Aes.SYM_Y)
    }

    override fun group(): Int? {
        return p.group()
    }

    override fun numeric(aes: Aes<Double>): Double? {
        return get(aes)
    }

    override fun <T> get(aes: Aes<T>): T? {
        return p[aes]
    }
}
