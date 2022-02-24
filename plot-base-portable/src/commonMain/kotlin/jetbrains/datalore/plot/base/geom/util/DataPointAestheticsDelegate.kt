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

open class DataPointAestheticsDelegate(private val p: DataPointAesthetics) :
    DataPointAesthetics {

    override fun index(): Int {
        return p.index()
    }

    override fun x(): Double? {
        return p.x()
    }

    override fun y(): Double? {
        return p.y()
    }

    override fun z(): Double? {
        return p.z()
    }

    override fun ymin(): Double? {
        return p.ymin()
    }

    override fun ymax(): Double? {
        return p.ymax()
    }

    override fun color(): Color? {
        return p.color()
    }

    override fun fill(): Color? {
        return p.fill()
    }

    override fun alpha(): Double? {
        return p.alpha()
    }

    override fun shape(): PointShape? {
        return p.shape()
    }

    override fun lineType(): LineType {
        return p.lineType()
    }

    override fun size(): Double? {
        return p.size()
    }

    override fun stacksize(): Double? {
        return p.stacksize()
    }

    override fun width(): Double? {
        return p.width()
    }

    override fun height(): Double? {
        return p.height()
    }

    override fun binwidth(): Double? {
        return p.binwidth()
    }

    override fun violinwidth(): Double? {
        return p.violinwidth()
    }

    override fun weight(): Double? {
        return p.weight()
    }

    override fun intercept(): Double? {
        return p.intercept()
    }

    override fun slope(): Double? {
        return p.slope()
    }

    override fun interceptX(): Double? {
        return p.interceptX()
    }

    override fun interceptY(): Double? {
        return p.interceptY()
    }

    override fun lower(): Double? {
        return p.lower()
    }

    override fun middle(): Double? {
        return p.middle()
    }

    override fun upper(): Double? {
        return p.upper()
    }

    override fun mapId(): Any {
        return p.mapId()
    }

    override fun frame(): String {
        return p.frame()
    }

    override fun speed(): Double? {
        return p.speed()
    }

    override fun flow(): Double? {
        return p.flow()
    }

    override fun xmin(): Double? {
        return p.xmin()
    }

    override fun xmax(): Double? {
        return p.xmax()
    }

    override fun xend(): Double? {
        return p.xend()
    }

    override fun yend(): Double? {
        return p.yend()
    }

    override fun label(): Any? {
        return p.label()
    }

    override fun family(): String {
        return p.family()
    }

    override fun fontface(): String {
        return p.fontface()
    }

    override fun hjust(): Any {
        return p.hjust()
    }

    override fun vjust(): Any {
        return p.vjust()
    }

    override fun angle(): Double? {
        return p.angle()
    }

    override fun symX(): Double? {
        return p.symX()
    }

    override fun symY(): Double? {
        return p.symY()
    }

    override fun group(): Int? {
        return p.group()
    }

    override fun numeric(aes: Aes<Double>): Double? {
        return p.numeric(aes)
    }

    override fun <T> get(aes: Aes<T>): T? {
        return p[aes]
    }
}
