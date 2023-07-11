/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import jetbrains.datalore.base.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent.Companion.FILL
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent.Companion.FILL_OPACITY
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent.Companion.STROKE
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent.Companion.STROKE_OPACITY
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent.Companion.STROKE_WIDTH
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent.Companion.TEXT_ANCHOR
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent.Companion.TEXT_DY

class SvgTSpanElement() : SvgElement(), SvgTextContent {

    companion object {
        private val X: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("x")
        private val Y: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("y")
    }

    override val elementName = "tspan"

    override val computedTextLength: Double
        get() = container().getPeer()!!.getComputedTextLength(this)

    constructor(text: String) : this() {

        setText(text)
    }

    constructor(x: Double, y: Double, text: String) : this(text) {

        setAttribute(X, x)
        setAttribute(Y, y)
    }

    fun x(): Property<Double?> {
        return getAttribute(X)
    }

    fun y(): Property<Double?> {
        return getAttribute(Y)
    }

    fun setText(text: String) {
        children().clear()
        addText(text)
    }

    fun addText(text: String) {
        val node = SvgTextNode(text)
        children().add(node)
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

    override fun textAnchor(): Property<String?> {
        return getAttribute(TEXT_ANCHOR)
    }

    override fun textDy(): Property<String?> {
        return getAttribute(TEXT_DY)
    }
}