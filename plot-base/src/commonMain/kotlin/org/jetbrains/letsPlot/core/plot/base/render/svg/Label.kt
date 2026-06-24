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
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.core.plot.base.render.text.TextBlockLayout
import org.jetbrains.letsPlot.core.plot.base.theme.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.math.roundToInt


class Label(
    val text: String,
    private val wrapWidth: Int = -1,
    private val markdown: Boolean = false
) : SvgComponent() {
    private val myLines: List<SvgTextElement>
    private val myLineAnchors = mutableListOf<HorizontalAnchor>()
    private var myTextColor: Color? = null
    private var myFillNone: Boolean = false
    private var myStrokeColor: Color? = null
    private var myStrokeWidth: Double? = null
    private var myStrokeLinejoin: String? = null
    private var myFontSize = 0.0
    private var myFontWeight: String? = null
    private var myFontFamily: String? = null
    private var myFontStyle: String? = null
    private var myTextLayout: TextBlockLayout? = null
    private var myHorizontalAnchor: HorizontalAnchor = RichText.DEF_HORIZONTAL_ANCHOR
    private var myVerticalAnchor: VerticalAnchor? = null
    private var xStart: Double? = null
    private var yStart = 0.0

    init {
        val renderedLines = renderLines()
        myLines = renderedLines.map(RichText.RenderedLine::element)
        myLineAnchors += renderedLines.map(RichText.RenderedLine::anchor)
        myLines.forEach(rootGroup.children()::add)
        updateStyleAttribute()
        verticalRepositionLines()
        horizontalRepositionLines()
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
                myLines.forEach { it.fillColor().set(value) }

                // duplicate in 'style' to override styles of container
                myTextColor = value
                updateStyleAttribute()
            }
        }
    }

    fun setFillNone() {
        myFillNone = true
        updateStyleAttribute()
    }

    fun textStrokeColor(): WritableProperty<Color?> {
        return object : WritableProperty<Color?> {
            override fun set(value: Color?) {
                // set attribute for svg->canvas mapping to work
                myLines.forEach { it.strokeColor().set(value) }

                // duplicate in 'style' to override styles of container
                myStrokeColor = value
                updateStyleAttribute()
            }
        }
    }

    fun setStrokeWidth(px: Double) {
        myLines.forEach { it.strokeWidth().set(px) }
        myStrokeWidth = px
        updateStyleAttribute()
    }

    fun setStrokeLinejoin(linejoin: String?) {
        myStrokeLinejoin = linejoin
        updateStyleAttribute()
    }

    fun setHorizontalAnchor(anchor: HorizontalAnchor) {
        myHorizontalAnchor = anchor
        horizontalRepositionLines()
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

    private fun updateStyleAttribute() {
        val styleAttr = Text.buildStyle(
            myTextColor,
            myFontSize,
            myFontWeight,
            myFontFamily,
            myFontStyle,
            myFillNone,
            myStrokeColor,
            myStrokeWidth,
            myStrokeLinejoin
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

    fun setTextLayout(textLayout: TextBlockLayout) {
        if (myLines.isEmpty()) return
        require(textLayout.lineBoxes.size == linesCount()) { "Line layout metrics count must match line count." }
        myTextLayout = textLayout
        verticalRepositionLines()
    }

    private fun verticalRepositionLines() {
        val textLayout = myTextLayout
        if (textLayout == null) {
            myLines.forEach { it.y().set(yStart) }
            return
        }

        val baselineOffsets = textLayout.baselineOffsets
        val totalBaselineShift = textLayout.baselineSpan

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

    // After changing font properties or anchor policy, RichText may regenerate the line subtree.
    // Keep the original line elements, but replace their direct children so line-level state
    // (style, classes, y, text-anchor) stays intact.
    private fun horizontalRepositionLines() {
        val renderedLines = renderLines()
        require(myLines.size == renderedLines.size) { "Line counts must be the same." }
        myLineAnchors.clear()
        myLineAnchors += renderedLines.map(RichText.RenderedLine::anchor)
        (myLines zip renderedLines).forEach { (originalLine, renderedLine) ->
            originalLine.replaceChildrenFrom(renderedLine.element)
        }
        xStart?.let { newX -> myLines.forEach { line -> line.x().set(newX) } }
        updateHorizontalAnchor()
    }

    private fun updateHorizontalAnchor() {
        (myLines zip myLineAnchors).forEach { (line, anchor) ->
            line.setAttribute(SvgConstants.SVG_TEXT_ANCHOR_ATTRIBUTE, toTextAnchor(anchor))
        }
    }

    fun linesCount() = myLines.size

    private fun renderLines(): List<RichText.RenderedLine> {
        val font = Font(
            family = FONT_FAMILY_REGISTRY.get(myFontFamily ?: FontFamily.DEF_FAMILY_NAME),
            size = myFontSize.roundToInt(),
            isBold = myFontWeight == "bold",
            isItalic = myFontStyle == "italic"
        )
        return RichText.renderLines(
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
