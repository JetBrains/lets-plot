package jetbrains.datalore.visualization.plot.base.geom.util

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.base.render.linetype.LineType
import jetbrains.datalore.visualization.plot.base.render.point.PointShape

open class DataPointAestheticsDelegate(private val myP: DataPointAesthetics) : DataPointAesthetics {

    override fun index(): Int {
        return myP.index()
    }

    override fun x(): Double? {
        return myP.x()
    }

    override fun y(): Double? {
        return myP.y()
    }

    override fun z(): Double? {
        return myP.z()
    }

    override fun ymin(): Double? {
        return myP.ymin()
    }

    override fun ymax(): Double? {
        return myP.ymax()
    }

    override fun color(): Color? {
        return myP.color()
    }

    override fun fill(): Color? {
        return myP.fill()
    }

    override fun alpha(): Double? {
        return myP.alpha()
    }

    override fun shape(): PointShape? {
        return myP.shape()
    }

    override fun lineType(): LineType {
        return myP.lineType()
    }

    override fun size(): Double? {
        return myP.size()
    }

    override fun width(): Double? {
        return myP.width()
    }

    override fun height(): Double? {
        return myP.height()
    }

    override fun weight(): Double? {
        return myP.weight()
    }

    override fun intercept(): Double? {
        return myP.intercept()
    }

    override fun slope(): Double? {
        return myP.slope()
    }

    override fun interceptX(): Double? {
        return myP.interceptX()
    }

    override fun interceptY(): Double? {
        return myP.interceptY()
    }

    override fun lower(): Double? {
        return myP.lower()
    }

    override fun middle(): Double? {
        return myP.middle()
    }

    override fun upper(): Double? {
        return myP.upper()
    }

    override fun mapId(): Any {
        return myP.mapId()
    }

    override fun frame(): String {
        return myP.frame()
    }

    override fun speed(): Double? {
        return myP.speed()
    }

    override fun flow(): Double? {
        return myP.flow()
    }

    override fun xmin(): Double? {
        return myP.xmin()
    }

    override fun xmax(): Double? {
        return myP.xmax()
    }

    override fun xend(): Double? {
        return myP.xend()
    }

    override fun yend(): Double? {
        return myP.yend()
    }

    override fun label(): String {
        return myP.label()
    }

    override fun family(): String {
        return myP.family()
    }

    override fun fontface(): String {
        return myP.fontface()
    }

    override fun hjust(): Any {
        return myP.hjust()
    }

    override fun vjust(): Any {
        return myP.vjust()
    }

    override fun angle(): Double? {
        return myP.angle()
    }

    override fun group(): Int? {
        return myP.group()
    }

    override fun numeric(aes: Aes<Double>): Double? {
        return myP.numeric(aes)
    }

    override fun <T> get(aes: Aes<T>): T? {
        return myP[aes]
    }
}
