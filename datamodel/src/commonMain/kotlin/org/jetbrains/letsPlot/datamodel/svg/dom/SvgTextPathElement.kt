/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.commons.values.Color

class SvgTextPathElement(): SvgElement(), SvgTextContent {

    companion object {
        val HREF: SvgAttributeSpec<String> =
            SvgAttributeSpec.createSpecNS(
                "href",
                XmlNamespace.XLINK_PREFIX,
                XmlNamespace.XLINK_NAMESPACE_URI
            )
        val START_OFFSET: SvgAttributeSpec<String> =
            SvgAttributeSpec.createSpec("startOffset")
    }
    override val elementName = "textPath"

    override val computedTextLength: Double
        get() = container().getPeer()!!.getComputedTextLength(this)

    // startOffset in length-percentage units
    constructor(text: String, pathId: String, startOffset: Double) : this() {
        setAttribute(HREF, "#$pathId")
        setAttribute(START_OFFSET, "$startOffset%")
        setText(text)
    }

    constructor(
        lines: List<String>,
        textHeight: Double,
        pathId: String,
        startOffset: Double
    ) : this() {
        setAttribute(HREF, "#$pathId")
        setAttribute(START_OFFSET, "$startOffset%")

        // add lines as tspan-elements
        val lineHeight = textHeight / lines.size
        val yStart = lineHeight - textHeight  // like vertical anchor = BOTTOM
        lines.forEachIndexed { index, line ->
            val dy = yStart + lineHeight * index
            addTSpan(line, dy)
        }
    }

    private fun setText(text: String) {
        children().clear()
        addText(text)
    }

    private fun addText(text: String) {
        val textNode = SvgTextNode(text)
        children().add(textNode)
    }

    private fun addTSpan(text: String, dy: Double) {
        val span = SvgTSpanElement(text)
        span.x().set(0.0)
        span.textDy().set(dy.toString())
        children().add(span)
    }

    override fun fill(): Property<SvgColor?> {
        return getAttribute(SvgTextContent.FILL)
    }

    override fun fillColor(): WritableProperty<Color?> {
        return SvgUtils.colorAttributeTransform(fill(), fillOpacity())
    }

    override fun fillOpacity(): Property<Double?> {
        return getAttribute(SvgTextContent.FILL_OPACITY)
    }

    override fun stroke(): Property<SvgColor?> {
        return getAttribute(SvgTextContent.STROKE)
    }

    override fun strokeColor(): WritableProperty<Color?> {
        return SvgUtils.colorAttributeTransform(stroke(), strokeOpacity())
    }

    override fun strokeOpacity(): Property<Double?> {
        return getAttribute(SvgTextContent.STROKE_OPACITY)
    }

    override fun strokeWidth(): Property<Double?> {
        return getAttribute(SvgTextContent.STROKE_WIDTH)
    }

    override fun textAnchor(): Property<String?> {
        return getAttribute(SvgTextContent.TEXT_ANCHOR)
    }

    override fun textDy(): Property<String?> {
        return getAttribute(SvgTextContent.TEXT_DY)
    }
}