/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.HorizontalAnchor
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.VerticalAnchor
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.toDY
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.toTextAnchor
import org.jetbrains.letsPlot.core.plot.base.render.text.LineLayoutMetrics
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.core.plot.base.theme.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.math.roundToInt


class Label(
    val text: String,
    private val wrapWidth: Int = -1,
    private val markdown: Boolean = false
) : SvgComponent() {
    private val myLines: List<SvgTextElement>
    private var myTextColor: Color? = null
    private var myFontSize = 0.0
    private var myFontWeight: String? = null
    private var myFontFamily: String? = null
    private var myFontStyle: String? = null
    private val myMetrics = mutableListOf<LineLayoutMetrics>()
    private var myHorizontalAnchor: HorizontalAnchor = RichText.DEF_HORIZONTAL_ANCHOR
    private var myVerticalAnchor: VerticalAnchor? = null
    private var xStart: Double? = null
    private var yStart = 0.0

    init {
        myLines = getLines()
        myLines.forEach(rootGroup.children()::add)
        updateStyleAttribute()
        verticalRepositionLines()
        horizontalRepositionLines(updateHorizontalAnchor = true)
    }

    override fun buildComponent() {
    }

    override fun addClassName(className: String) {
        myLines.forEach { it.addClass(className) }
    }

    fun textColor(): WritableProperty<Color?> {
        return object : WritableProperty<Color?> {
            override fun set(value: Color?) {
                // set attribute for svg->canvas mapping to work
                myLines.forEach(SvgTextElement::fillColor)

                // duplicate in 'style' to override styles of container
                myTextColor = value
                updateStyleAttribute()
            }
        }
    }

    fun setHorizontalAnchor(anchor: HorizontalAnchor) {
        myHorizontalAnchor = anchor
        horizontalRepositionLines(updateHorizontalAnchor = true)
    }

    fun setVerticalAnchor(anchor: VerticalAnchor) {
        myVerticalAnchor = anchor
        myLines.forEach {
            it.setAttribute(SvgConstants.SVG_TEXT_DY_ATTRIBUTE, toDY(anchor))
        }
        verticalRepositionLines()
    }

    fun setFontSize(px: Double) {
        myFontSize = px
        updateStyleAttribute()
        horizontalRepositionLines()
    }

    /**
     * @param cssName : normal, bold, bolder, lighter
     */
    fun setFontWeight(cssName: String?) {
        myFontWeight = cssName
        updateStyleAttribute()
        horizontalRepositionLines()
    }

    /**
     * @param cssName : normal, italic, oblique
     */
    fun setFontStyle(cssName: String?) {
        myFontStyle = cssName
        updateStyleAttribute()
        horizontalRepositionLines()
    }

    /**
     * @param fontFamily : for example 'sans-serif' or 'Times New Roman'
     */
    fun setFontFamily(fontFamily: String?) {
        myFontFamily = fontFamily
        updateStyleAttribute()
        horizontalRepositionLines()
    }

    fun setTextOpacity(value: Double?) {
        myLines.forEach { it.fillOpacity().set(value) }
    }

    private fun updateStyleAttribute() {
        val styleAttr = Text.buildStyle(
            myTextColor,
            myFontSize,
            myFontWeight,
            myFontFamily,
            myFontStyle
        )
        myLines.forEach { it.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, styleAttr) }
    }

    fun setX(x: Double) {
        xStart = x
        horizontalRepositionLines()
    }

    fun setY(y: Double) {
        yStart = y
        verticalRepositionLines()
    }

    fun setConstantLineLayoutMetrics(v: LineLayoutMetrics) {
        setLineLayoutMetrics(List(linesCount()) { v })
    }

    fun setLineLayoutMetrics(values: List<LineLayoutMetrics>) {
        myMetrics.clear()
        if (myLines.isEmpty()) return
        require(values.size == linesCount()) { "Line layout metrics count must match line count." }
        myMetrics.addAll(values)
        verticalRepositionLines()
    }

    private fun verticalRepositionLines() {
        if (myMetrics.isEmpty()) {
            myLines.forEach { it.y().set(yStart) }
            return
        }

        val baselineOffsets = myMetrics
            .zipWithNext { prev, next -> prev.descent + next.ascent }
            .runningFold(0.0, Double::plus)
        val totalBaselineShift = baselineOffsets.last()

        val firstLineY = yStart - when (myVerticalAnchor) {
            VerticalAnchor.TOP -> 0.0
            VerticalAnchor.CENTER -> totalBaselineShift / 2.0
            VerticalAnchor.BOTTOM -> totalBaselineShift
            else -> 0.0
        }

        myLines.forEachIndexed { index, elem ->
            elem.y().set(firstLineY + baselineOffsets[index])
        }
    }

    // After changing some font properties, RichText may regenerate the line subtree
    // with updated tspan x/text values. Keep the original line elements, but replace
    // their direct children so line-level state (style, classes, y, text-anchor) stays intact.
    private fun horizontalRepositionLines(updateHorizontalAnchor: Boolean = false) {
        // Update rendered subtree inside each line.
        val recalculatedLines = getLines()
        require(myLines.size == recalculatedLines.size) { "Line counts must be the same." }
        (myLines zip recalculatedLines).forEach { (originalLine, recalculatedLine) ->
            originalLine.replaceChildrenFrom(recalculatedLine)
        }
        // Update x-attribute of lines
        xStart?.let { newX -> myLines.forEach { line -> line.x().set(newX) } }
        // Update text-anchor attribute of lines
        if (updateHorizontalAnchor) {
            updateHorizontalAnchor()
        }
    }

    private fun updateHorizontalAnchor() {
        myLines.forEach { line ->
            val firstTSpanHasExplicitX = findFirstTSpan(line)?.x()?.get() != null
            val anchorAttr = when {
                firstTSpanHasExplicitX -> toTextAnchor(HorizontalAnchor.LEFT)
                else -> toTextAnchor(myHorizontalAnchor)
            }
            line.setAttribute(SvgConstants.SVG_TEXT_ANCHOR_ATTRIBUTE, anchorAttr)
        }
    }

    fun linesCount() = myLines.size

    private fun getLines(): List<SvgTextElement> {
        val font = Font(
            family = FONT_FAMILY_REGISTRY.get(myFontFamily ?: FontFamily.DEF_FAMILY_NAME),
            size = myFontSize.roundToInt(),
            isBold = myFontWeight == "bold",
            isItalic = myFontStyle == "italic"
        )
        return RichText.toSvg(
            text,
            font,
            wrapWidth,
            markdown = markdown,
            anchor = myHorizontalAnchor,
            initialX = xStart
        )
    }

    companion object {
        private val FONT_FAMILY_REGISTRY = DefaultFontFamilyRegistry()

        fun splitLines(text: String) = text.split('\n').map(String::trim)

        private fun findFirstTSpan(root: SvgNode): SvgTSpanElement? =
            when (root) {
                is SvgTSpanElement -> root
                else -> root.children().firstNotNullOfOrNull { findFirstTSpan(it) }
            }

        private fun SvgTextElement.replaceChildrenFrom(other: SvgTextElement) {
            val newChildren = other.children().toList()
            children().clear()
            newChildren.forEach { child ->
                child.removeFromParent()
                children().add(child)
            }
        }
    }
}
