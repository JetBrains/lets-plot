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
import org.jetbrains.letsPlot.core.plot.base.render.text.LineElement
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.core.plot.base.render.text.TextBlockLayout
import org.jetbrains.letsPlot.core.plot.base.theme.DefaultFontFamilyRegistry
import kotlin.math.roundToInt


class Label(
    val text: String,
    private val wrapWidth: Int = -1,
    private val markdown: Boolean = false
) : SvgComponent() {
    private val myLines = mutableListOf<LineElement>()
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

    init {
        installLines(renderLines())
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
                // set attribute for svg->canvas mapping to work — recurses into vector groups so
                // every text element and every path glyph picks up the new color.
                myLines.forEach { it.applyColor(value) }

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
        myLines.forEach { it.setVerticalAnchor(anchor, myFontSize) }
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
        myLines.forEach { it.applyStyle(styleAttr) }
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
            myLines.forEach { it.setY(yStart) }
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

        myLines.forEachIndexed { index, line ->
            line.setY(firstLineY + baselineOffsets[index])
        }
    }

    private fun installLines(rendered: List<RichText.RenderedLine>) {
        myLines.forEach { it.element.removeFromParent() }
        myLines.clear()
        myLines += rendered.map { it.line }
        myLineAnchors.clear()
        myLineAnchors += rendered.map { it.anchor }
        myLines.forEach { rootGroup.children().add(it.element) }
    }

    // After changing font properties or anchor policy, RichText may regenerate the line subtree.
    // Keep the original line elements, but replace their direct children so line-level state
    // (style, classes, y, text-anchor) stays intact.
    private fun horizontalRepositionLines() {
        val renderedLines = renderLines()
        require(myLines.size == renderedLines.size) { "Line counts must be the same." }
        val kindStable = (myLines zip renderedLines).all { (old, rendered) -> old.canAbsorb(rendered.line) }
        if (kindStable) {
            (myLines zip renderedLines).forEach { (old, rendered) -> old.replaceChildrenFrom(rendered.line) }
            myLineAnchors.clear()
            myLineAnchors += renderedLines.map { it.anchor }
        } else {
            installLines(renderedLines)
        }
        // Regenerated children of a mixed line (plain text + vector LaTeX formula) are born without
        // the font 'style' attribute: it lived on the previous inner <text>, which was just replaced.
        // Unlike a pure-text line — whose <text> wrapper is preserved across child replacement — the
        // fresh inner <text> would otherwise fall back to the renderer's default font size/family/face.
        // Re-apply the style so the prefix matches the formula's font size (and family/weight/style).
        updateStyleAttribute()
        // Regenerated children (e.g. vector LaTeX glyph paths) are born with the default color and
        // carry no inheritable parent style, unlike legacy <tspan>s under a persistent <text>.
        // Re-apply an explicitly-set text color so the fresh paths pick it up. Guard on non-null:
        // labels colored via a stylesheet class never set myTextColor, and clobbering their fill
        // with null here would hide them (titles, axis labels, legend, caption).
        myLines.forEach { it.setVerticalAnchor(myVerticalAnchor, myFontSize) }
        myTextColor?.let { color -> myLines.forEach { it.applyColor(color) } }
        xStart?.let { newX -> myLines.forEach { it.setX(newX) } }
        if (!kindStable) {
            verticalRepositionLines()
        }
        updateHorizontalAnchor()
    }

    private fun updateHorizontalAnchor() {
        (myLines zip myLineAnchors).forEach { (line, anchor) ->
            line.setHorizontalAnchor(anchor)
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

    companion object {
        private val FONT_FAMILY_REGISTRY = DefaultFontFamilyRegistry()

        fun splitLines(text: String) = text.split('\n').map(String::trim)
    }
}
