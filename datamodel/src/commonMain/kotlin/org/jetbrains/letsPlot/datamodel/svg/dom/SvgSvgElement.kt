/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgContainer.Companion.CLIP_PATH
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgContainer.Companion.OPACITY

class SvgSvgElement constructor() :
    SvgStylableElement(),
    SvgContainer,
    SvgLocatable {

    companion object {
        val X: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("x")
        val Y: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("y")
        val WIDTH: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec(SvgConstants.WIDTH)
        val HEIGHT: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec(SvgConstants.HEIGHT)
        val VIEW_BOX: SvgAttributeSpec<ViewBoxRectangle> =
            SvgAttributeSpec.createSpec("viewBox")
        val DISPLAY: SvgAttributeSpec<String> =
            SvgAttributeSpec.createSpec(SvgConstants.DISPLAY)
    }

    override val elementName = "svg"

    override val bBox: DoubleRectangle
        get() = container().getPeer()!!.getBBox(this)

    init {
        // Make SVG a block-element.
        // By default, in HTML SVG is an inline element.
        // As result the parent DIV height may sometimes be a few pixels larger than the height of the SVG itself,
        // because of the vertical alignment of inline elements, which is "baseline".
        setAttribute(DISPLAY, "block")
    }

    constructor(width: Double, height: Double) : this() {

        setAttribute(WIDTH, width)
        setAttribute(HEIGHT, height)
    }

    fun setStyle(css: SvgCssResource) {
        children().add(SvgStyleElement(css))
    }

    fun x(): Property<Double?> {
        return getAttribute(X)
    }

    fun y(): Property<Double?> {
        return getAttribute(Y)
    }

    fun width(): Property<Double?> {
        return getAttribute(WIDTH)
    }

    fun height(): Property<Double?> {
        return getAttribute(HEIGHT)
    }

    fun viewBox(): Property<ViewBoxRectangle?> {
        return getAttribute(VIEW_BOX)
    }

    fun viewBoxRect(): WritableProperty<DoubleRectangle> {
        return object : WritableProperty<DoubleRectangle> {
            override fun set(value: DoubleRectangle) {
                viewBox().set(ViewBoxRectangle(value))
            }

        }
    }

    override fun opacity(): Property<Double?> {
        return getAttribute(OPACITY)
    }

    override fun clipPath(): Property<SvgIRI?> {
        return getAttribute(CLIP_PATH)
    }

    override fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.invertTransform(this, point)
    }

    override fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.applyTransform(this, point)
    }


    class ViewBoxRectangle {
        private var myX: Double = 0.toDouble()
        private var myY: Double = 0.toDouble()
        private var myWidth: Double = 0.toDouble()
        private var myHeight: Double = 0.toDouble()

        constructor(x: Double, y: Double, width: Double, height: Double) {
            myX = x
            myY = y
            myWidth = width
            myHeight = height
        }

        constructor(rect: DoubleRectangle) {
            myX = rect.origin.x
            myY = rect.origin.y
            myWidth = rect.dimension.x
            myHeight = rect.dimension.y
        }

//        constructor(rect: Rectangle) {
//            myX = rect.origin.x
//            myY = rect.origin.y
//            myWidth = rect.dimension.x
//            myHeight = rect.dimension.y
//        }

        override fun toString(): String {
            return "$myX $myY $myWidth $myHeight"
        }
    }
}