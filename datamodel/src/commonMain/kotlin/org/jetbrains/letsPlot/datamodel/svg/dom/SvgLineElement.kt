/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape.Companion.FILL
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape.Companion.FILL_OPACITY
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape.Companion.STROKE
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape.Companion.STROKE_DASHARRAY
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape.Companion.STROKE_DASHOFFSET
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape.Companion.STROKE_OPACITY
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape.Companion.STROKE_WIDTH
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransformable.Companion.TRANSFORM

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

    override fun strokeDashArray(): Property<String?> {
        return getAttribute(STROKE_DASHARRAY)
    }

    override fun strokeDashOffset(): Property<Double?> {
        return getAttribute(STROKE_DASHOFFSET)
    }

    override fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.invertTransform(this, point)
    }

    override fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.applyTransform(this, point)
    }
}