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
import org.jetbrains.letsPlot.core.plot.base.theme.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode
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
    private var myLineHeight = 0.0
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

    fun setLineHeight(v: Double) {
        myLineHeight = v
        verticalRepositionLines()
    }

    private fun verticalRepositionLines() {
        val totalHeightShift = myLineHeight * (myLines.size - 1)

        val adjustedYStart = yStart - when (myVerticalAnchor) {
            VerticalAnchor.TOP -> 0.0
            VerticalAnchor.CENTER -> totalHeightShift / 2
            VerticalAnchor.BOTTOM -> totalHeightShift
            else -> 0.0
        }

        myLines.forEachIndexed { index, elem ->
            elem.y().set(adjustedYStart + myLineHeight * index)
        }
    }

    // After changing some font properties, some SVG text attributes (like x) may change.
    // So, we need to recalculate them and update the original elements.
    private fun horizontalRepositionLines(updateHorizontalAnchor: Boolean = false) {
        // Update tspan elements in lines
        val recalculatedLines = getLines()
        require(myLines.size == recalculatedLines.size) { "Line counts must be the same." }
        (myLines zip recalculatedLines).forEach { (originalLine, recalculatedLine) ->
            walkPair(originalLine, recalculatedLine) { originalNode, recalculatedNode ->
                if (originalNode is SvgTSpanElement && recalculatedNode is SvgTSpanElement) {
                    originalNode.x().set(recalculatedNode.x().get()) // x-attribute of tspan's in fraction may change
                    originalNode.copyText(recalculatedNode) // count of fraction bar symbols "–" may change
                }
            }
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
            initialX = xStart ?: 0.0
        )
    }

    companion object {
        private val FONT_FAMILY_REGISTRY = DefaultFontFamilyRegistry()

        fun splitLines(text: String) = text.split('\n').map(String::trim)

        private fun walkPair(
            node1: SvgNode,
            node2: SvgNode,
            action: (SvgNode, SvgNode) -> Unit
        ) {
            require(node1::class == node2::class) { "Node classes must be the same: ${node1::class} != ${node2::class}" }
            val children1 = node1.children()
            val children2 = node2.children()
            require(children1.size == children2.size) { "Node lists must have the same size." }
            (children1 zip children2).forEach { (child1, child2) ->
                walkPair(child1, child2, action)
            }
            action(node1, node2)
        }

        private fun findFirstTSpan(root: SvgNode): SvgTSpanElement? =
            when (root) {
                is SvgTSpanElement -> root
                else -> root.children().firstNotNullOfOrNull { findFirstTSpan(it) }
            }

        // Copy text content from another tspan element, or clear text if other has no text node.
        // Does not copy any attributes, and does not change any attributes of this element.
        // Uses the fact that tspan has either 0 or 1 text node of type SvgTextNode, see SvgTSpanElement.setText()/addText().
        fun SvgTSpanElement.copyText(other: SvgTSpanElement) {
            val otherTextNode = other.children().singleOrNull() as? SvgTextNode
            if (otherTextNode == null) {
                children().clear()
                return
            }
            val text = otherTextNode.textContent().get()
            val textNode = children().singleOrNull() as? SvgTextNode
            if (textNode == null) {
                setText(text)
            } else {
                textNode.textContent().set(text)
            }
        }
    }
}