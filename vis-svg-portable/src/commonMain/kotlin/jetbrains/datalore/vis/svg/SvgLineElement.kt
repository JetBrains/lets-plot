/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.svg.SvgShape.Companion.FILL
import jetbrains.datalore.vis.svg.SvgShape.Companion.FILL_OPACITY
import jetbrains.datalore.vis.svg.SvgShape.Companion.STROKE
import jetbrains.datalore.vis.svg.SvgShape.Companion.STROKE_OPACITY
import jetbrains.datalore.vis.svg.SvgShape.Companion.STROKE_WIDTH
import jetbrains.datalore.vis.svg.SvgTransformable.Companion.TRANSFORM

class SvgLineElement() : SvgGraphicsElement(), SvgTransformable,
    SvgShape {

    companion object {
        val X1: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("x1")
        val Y1: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("y1")
        val X2: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("x2")
        val Y2: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("y2")
    }

    override val elementName = "line"

    override val bBox: DoubleRectangle
        get() = container().getPeer()!!.getBBox(this)

    constructor(x1: Double, y1: Double, x2: Double, y2: Double) : this() {

        setAttribute(X1, x1)
        setAttribute(Y1, y1)
        setAttribute(X2, x2)
        setAttribute(Y2, y2)
    }

    fun x1(): Property<Double?> {
        return getAttribute(X1)
    }

    fun y1(): Property<Double?> {
        return getAttribute(Y1)
    }

    fun x2(): Property<Double?> {
        return getAttribute(X2)
    }

    fun y2(): Property<Double?> {
        return getAttribute(Y2)
    }

    override fun transform(): Property<SvgTransform?> {
        return getAttribute(TRANSFORM)
    }

    override fun fill(): Property<SvgColor?> {
        return getAttribute(FILL)
    }

    override fun fillColor(): WritableProperty<Color?> {
        return SvgUtils.colorAttributeTransform(fill(), fillOpacity())
    }

    override fun fillOpacity(): Property<Double?> {
        return getAttribute(FILL_OPACITY)
    }

    override fun stroke(): Property<SvgColor?> {
        return getAttribute(STROKE)
    }

    override fun strokeColor(): WritableProperty<Color?> {
        return SvgUtils.colorAttributeTransform(stroke(), strokeOpacity())
    }

    override fun strokeOpacity(): Property<Double?> {
        return getAttribute(STROKE_OPACITY)
    }

    override fun strokeWidth(): Property<Double?> {
        return getAttribute(STROKE_WIDTH)
    }

    override fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.invertTransform(this, point)
    }

    override fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.applyTransform(this, point)
    }
}