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
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape.Companion.STROKE_OPACITY
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape.Companion.STROKE_WIDTH
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransformable.Companion.TRANSFORM

class SvgEllipseElement() : SvgGraphicsElement(),
    SvgTransformable, SvgShape {

    companion object {
        val CX: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("cx")
        val CY: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("cy")
        val RX: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("rx")
        val RY: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("ry")
    }

    override val elementName = "ellipse"

    override val bBox: DoubleRectangle
        get() = container().getPeer()!!.getBBox(this)

    constructor(cx: Double, cy: Double, rx: Double, ry: Double) : this() {

        setAttribute(CX, cx)
        setAttribute(CY, cy)
        setAttribute(RX, rx)
        setAttribute(RY, ry)
    }

    fun cx(): Property<Double?> {
        return getAttribute(CX)
    }

    fun cy(): Property<Double?> {
        return getAttribute(CY)
    }

    fun rx(): Property<Double?> {
        return getAttribute(RX)
    }

    fun ry(): Property<Double?> {
        return getAttribute(RY)
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