/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.formatting.number.PowerFormat
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.json.escape
import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent.Companion.FILL
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent.Companion.FILL_OPACITY
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent.Companion.STROKE
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent.Companion.STROKE_OPACITY
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent.Companion.STROKE_WIDTH
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent.Companion.TEXT_ANCHOR
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent.Companion.TEXT_DY
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransformable.Companion.TRANSFORM

class SvgTextElement() : SvgGraphicsElement(), SvgTransformable,
    SvgTextContent {

    companion object {
        val X: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("x")
        val Y: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("y")
    }

    override val elementName = "text"

    override val computedTextLength: Double
        get() = container().getPeer()!!.getComputedTextLength(this)

    override val bBox: DoubleRectangle
        get() = container().getPeer()!!.getBBox(this)

    constructor(content: String) : this() {

        setTextNode(content)
    }

    constructor(x: Double, y: Double, content: String) : this() {

        setAttribute(X, x)
        setAttribute(Y, y)
        setTextNode(content)
    }

    fun x(): Property<Double?> {
        return getAttribute(X)
    }

    fun y(): Property<Double?> {
        return getAttribute(Y)
    }

    override fun transform(): Property<SvgTransform?> {
        return getAttribute(TRANSFORM)
    }

    fun setTextNode(text: String) {
        children().clear()
        addTextNode(text)
    }

    fun addTextNode(text: String) {
        val textNode = SvgTextNode(text)
        children().add(textNode)
    }

    fun setTSpan(tspan: SvgTSpanElement) {
        children().clear()
        addTSpan(tspan)
    }

    fun setTSpan(text: String) {
        children().clear()
        addTSpan(text)
    }

    fun addTSpan(tspan: SvgTSpanElement) {
        children().add(tspan)
    }

    fun addTSpan(text: String) {
        children().add(SvgTSpanElement(text))
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

    override fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.invertTransform(this, point)
    }

    override fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.applyTransform(this, point)
    }

    fun asPowerDegreeFormula(): SvgTextElement {
        if (children().isEmpty()) {
            return this
        }
        val text = (children()[0] as SvgTextNode).textContent().get()
        val powerDegree = PowerDegree.fromText(text) ?: return this
        return powerDegree.toSvgTextElement(this)
    }

    data class PowerDegree(
        private val sign: String,
        private val coefficient: String,
        private val base: String,
        private val degree: String,
        private val prefix: String,
        private val postfix: String
    ) {
        private val multiplier: String
            get() = if (coefficient.isEmpty()) {
                ""
            } else {
                "${coefficient}·"
            }

        fun toSvgTextElement(origin: SvgTextElement): SvgTextElement {
            return if (prefix.isEmpty() && postfix.isEmpty()) {
                toPrettyElement(origin)
            } else {
                toSimpleElement(origin)
            }
        }

        private fun toPrettyElement(origin: SvgTextElement): SvgTextElement {
            val baseTSpan = SvgTSpanElement("$sign$multiplier$base")
            val degreeTSpan = SvgTSpanElement(degree)
            degreeTSpan.setAttribute("baseline-shift", "super")
            degreeTSpan.setAttribute("font-size", "75%")
            val formulaTextElement = SvgTextElement()
            SvgUtils.copyAttributes(origin, formulaTextElement)
            formulaTextElement.addTSpan(baseTSpan)
            formulaTextElement.addTSpan(degreeTSpan)
            return formulaTextElement
        }

        private fun toSimpleElement(origin: SvgTextElement): SvgTextElement {
            val formulaTextElement = SvgTextElement()
            SvgUtils.copyAttributes(origin, formulaTextElement)
            formulaTextElement.addTextNode("$prefix$sign$multiplier$base^($degree)$postfix")
            return formulaTextElement
        }

        companion object {
            fun fromText(text: String): PowerDegree? {
                val powerDegreePattern = """^(?<prefix>.*)\\\(\s*(?<sign>-?)((?<coefficient>\d?)(\s*${PowerFormat.MULTIPLICATION_OPERATOR.escape()}\s*|\s+))?(?<base>\d+)\^(\{\s*)?(?<degree>-?\d+)(\s*\})?\s*\\\)(?<postfix>.*)${'$'}""".toRegex()
                val match = powerDegreePattern.find(text) ?: return null

                val groups = match.groups as MatchNamedGroupCollection
                val sign = groups["sign"]?.value ?: ""
                val coefficient = groups["coefficient"]?.value ?: ""
                val base = groups["base"]!!.value
                val degree = groups["degree"]!!.value
                val prefix = groups["prefix"]?.value ?: ""
                val postfix = groups["postfix"]?.value ?: ""
                return PowerDegree(sign, coefficient, base, degree, prefix, postfix)
            }
        }
    }
}