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

class SvgPathElement() : SvgGraphicsElement(), SvgTransformable,
    SvgShape {

    companion object {
        val FILL_RULE: SvgAttributeSpec<FillRule> = SvgAttributeSpec.createSpec("fill-rule")
        val D: SvgAttributeSpec<SvgPathData> = SvgAttributeSpec.createSpec("d")
        val STROKE_MITER_LIMIT: SvgAttributeSpec<Double> = SvgAttributeSpec.createSpec("stroke-miterlimit")
    }

    override val elementName = "path"

    override val bBox: DoubleRectangle
        get() = container().getPeer()!!.getBBox(this)

    constructor(d: SvgPathData) : this() {
        setAttribute(D, d)
    }

    fun fillRule(): Property<FillRule?> {
        return getAttribute(FILL_RULE)
    }

    fun d(): Property<SvgPathData?> {
        return getAttribute(D)
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

    fun strokeMiterLimit(): Property<Double?> {
        return getAttribute(STROKE_MITER_LIMIT)
    }

    override fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.invertTransform(this, point)
    }

    override fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.applyTransform(this, point)
    }

    enum class FillRule(private val myAttrString: String) {
        EVEN_ODD("evenodd"),
        NON_ZERO("nonzero");

        override fun toString(): String {
            return myAttrString
        }
    }
}