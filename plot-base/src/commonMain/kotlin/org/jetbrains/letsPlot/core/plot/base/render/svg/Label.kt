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
import org.jetbrains.letsPlot.core.plot.base.render.text.Latex
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.core.plot.base.render.text.TextBlockLayout
import org.jetbrains.letsPlot.core.plot.base.theme.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import kotlin.math.roundToInt


class Label(
    val text: String,
    private val wrapWidth: Int = -1,
    private val markdown: Boolean = false
) : SvgComponent() {
    private val myLines: List<SvgElement>
    private val myLineAnchors = mutableListOf<HorizontalAnchor>()
    private var myTextColor: Color? = null
    private var myFontSize = 0.0
    private var myFontWeight: String? = null
    private var myFontFamily: String? = null
    private var myFontStyle: String? = null
    private var myTextLayout: TextBlockLayout? = null
    private var myHorizontalAnchor: HorizontalAnchor = RichText.DEF_HORIZONTAL_ANCHOR
    private var myVerticalAnchor: VerticalAnchor? = null
    private var xStart: Double? = null
    private var yStart = 0.0

    // Track per-line x and y separately so setLineX / setLineY can compose a single transform on
    // group lines without losing the previously-set axis.
    private val myLineX = mutableMapOf<SvgElement, Double>()
    private val myLineY = mutableMapOf<SvgElement, Double>()

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
        myLines.forEach { line ->
            if (line is org.jetbrains.letsPlot.datamodel.svg.dom.SvgStylableElement) {
                line.addClass(className)
            }
        }
    }

    fun textColor(): WritableProperty<Color?> {
        return object : WritableProperty<Color?> {
            override fun set(value: Color?) {
                // set attribute for svg->canvas mapping to work — recurses into vector groups so
                // every text element and every path glyph picks up the new color.
                myLines.forEach { applyFillColor(it, value) }

                // duplicate in 'style' to override styles of container
                myTextColor = value
                updateStyleAttribute()
            }
        }
    }

    fun setHorizontalAnchor(anchor: HorizontalAnchor) {
        myHorizontalAnchor = anchor
        horizontalRepositionLines()
    }

    fun setVerticalAnchor(anchor: VerticalAnchor) {
        myVerticalAnchor = anchor
        myLines.forEach {
            // text-dy is only meaningful on <text>; mixed-line group wrappers ignore it (their
            // vertical positioning comes from the line's transform set by verticalRepositionLines).
            if (it is SvgTextElement) {
                it.setAttribute(SvgConstants.SVG_TEXT_DY_ATTRIBUTE, toDY(anchor))
            }
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
            myFontStyle
        )
        myLines.forEach { applyStyle(it, styleAttr) }
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
            myLines.forEach { setLineY(it, yStart) }
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
            setLineY(elem, firstLineY + baselineOffsets[index])
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
            replaceLineChildrenFrom(originalLine, renderedLine.element)
        }
        xStart?.let { newX -> myLines.forEach { line -> setLineX(line, newX) } }
        updateHorizontalAnchor()
    }

    private fun updateHorizontalAnchor() {
        (myLines zip myLineAnchors).forEach { (line, anchor) ->
            // text-anchor is only meaningful on <text>. Mixed-line groups use a line origin shift
            // (computed in RichText.renderPlans) instead, so they need no text-anchor attribute.
            if (line is SvgTextElement) {
                line.setAttribute(SvgConstants.SVG_TEXT_ANCHOR_ATTRIBUTE, toTextAnchor(anchor))
            }
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
            anchor = myHorizontalAnchor
        )
    }

    private fun setLineY(el: SvgElement, y: Double) {
        myLineY[el] = y
        when (el) {
            is SvgTextElement -> el.y().set(y)
            is SvgGElement -> el.transform().set(buildLineTransform(el))
            else -> error("Unexpected line element type: ${el::class.simpleName}")
        }
    }

    private fun setLineX(el: SvgElement, x: Double) {
        myLineX[el] = x
        when (el) {
            is SvgTextElement -> el.x().set(x)
            is SvgGElement -> el.transform().set(buildLineTransform(el))
            else -> error("Unexpected line element type: ${el::class.simpleName}")
        }
    }

    private fun buildLineTransform(el: SvgElement): SvgTransform {
        val tx = myLineX[el] ?: 0.0
        val ty = myLineY[el] ?: 0.0
        return SvgTransformBuilder().translate(tx, ty).build()
    }

    private fun applyFillColor(el: SvgElement, color: Color?) {
        when (el) {
            is SvgTextElement -> el.fillColor().set(color)
            is SvgPathElement -> {
                val isVectorBBoxGuide = el.classAttribute().get()
                    ?.split(' ')
                    ?.contains(Latex.VECTOR_BBOX_CLASS) == true
                if (!isVectorBBoxGuide) {
                    el.fillColor().set(color)
                }
            }
            is SvgGElement -> el.children().forEach { child ->
                if (child is SvgElement) applyFillColor(child, color)
            }
        }
    }

    private fun applyStyle(el: SvgElement, styleAttr: String) {
        when (el) {
            is SvgTextElement -> el.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, styleAttr)
            // Style on the outer group is inherited by descendant <text> elements (font-family,
            // fill, etc.). We still recurse so any direct <text> child gets the attribute set
            // explicitly — some downstream consumers don't propagate inherited CSS.
            is SvgGElement -> el.children().forEach { child ->
                if (child is SvgElement) applyStyle(child, styleAttr)
            }
        }
    }

    // Replace children of an existing line element with the children of a freshly rendered line.
    // Both arguments must be the same kind of element (text or group); the freshly rendered shape
    // is determined by the same input that produced the original line element.
    private fun replaceLineChildrenFrom(original: SvgElement, fresh: SvgElement) {
        when {
            original is SvgTextElement && fresh is SvgTextElement -> moveChildren(original, fresh)
            original is SvgGElement && fresh is SvgGElement -> moveChildren(original, fresh)
            else -> error("Line element type changed across re-renders: ${original::class.simpleName} vs ${fresh::class.simpleName}")
        }
    }

    private fun moveChildren(target: SvgElement, source: SvgElement) {
        val newChildren = source.children().toList()
        target.children().clear()
        newChildren.forEach { child ->
            child.removeFromParent()
            target.children().add(child)
        }
    }

    companion object {
        private val FONT_FAMILY_REGISTRY = DefaultFontFamilyRegistry()

        fun splitLines(text: String) = text.split('\n').map(String::trim)
    }
}
