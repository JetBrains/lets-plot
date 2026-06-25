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
                // set attribute for svg->canvas mapping to work
                myLines.forEach { it.applyColor(value) }

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
                myLines.forEach { it.applyStrokeColor(value) }

                // duplicate in 'style' to override styles of container
                myStrokeColor = value
                updateStyleAttribute()
            }
        }
    }

    fun setStrokeWidth(px: Double) {
        myLines.forEach { it.applyStrokeWidth(px) }
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
            myFontStyle,
            myFillNone,
            myStrokeColor,
            myStrokeWidth,
            myStrokeLinejoin
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
        myLines += rendered.map(RichText.RenderedLine::line)
        myLineAnchors.clear()
        myLineAnchors += rendered.map(RichText.RenderedLine::anchor)
        myLines.forEach { rootGroup.children().add(it.element) }
    }

    // After changing font properties or anchor policy, RichText may regenerate the line subtree.
    // Keep the original line elements, but replace their direct children so line-level state
    // (style, classes, y, text-anchor) stays intact.
    private fun horizontalRepositionLines() {
        val renderedLines = renderLines()
        require(myLines.size == renderedLines.size) { "Line counts must be the same." }
        // Check that each old/rendered pair has the same line type.
        val lineKindsStable = (myLines zip renderedLines).all { (old, rendered) -> old.canAbsorb(rendered.line) }
        if (lineKindsStable) {
            (myLines zip renderedLines).forEach { (old, rendered) -> old.replaceChildrenFrom(rendered.line) }
            myLineAnchors.clear()
            myLineAnchors += renderedLines.map(RichText.RenderedLine::anchor)
        } else {
            installLines(renderedLines)
        }
        // Re-apply text style after replacing mixed-line children: fresh inner <text> nodes lose font settings.
        updateStyleAttribute()
        myLines.forEach { it.setVerticalAnchor(myVerticalAnchor, myFontSize) }
        // Re-apply explicit text color to regenerated vector glyphs; leave stylesheet-colored labels untouched.
        myTextColor?.let { color -> myLines.forEach { it.applyColor(color) } }
        xStart?.let { newX -> myLines.forEach { it.setX(newX) } }
        if (!lineKindsStable) {
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
