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

    private fun horizontalRepositionLines() {
        (myLines zip getLines()).forEach { (originalLine, recalculatedLine) ->
            var firstTSpanHasExplicitX = false
            forEachNodePair(originalLine.children(), recalculatedLine.children()) { original, recalculated, isFirstTSpan ->
                if (original is SvgTSpanElement && recalculated is SvgTSpanElement) {
                    if (isFirstTSpan && recalculated.x().get() != null) {
                        firstTSpanHasExplicitX = true
                    }

                    original.x().set(recalculated.x().get())
                }
            }
            val anchorAttr = when {
                firstTSpanHasExplicitX -> toTextAnchor(HorizontalAnchor.LEFT)
                else -> toTextAnchor(myHorizontalAnchor)
            }

            originalLine.setAttribute(SvgConstants.SVG_TEXT_ANCHOR_ATTRIBUTE, anchorAttr)
            xStart?.let { originalLine.x().set(it) }
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

        private fun forEachNodePair(
            nodes1: Iterable<SvgNode>,
            nodes2: Iterable<SvgNode>,
            action: (SvgNode, SvgNode, Boolean) -> Unit
        ) {
            val stack = ArrayDeque<Pair<Iterator<SvgNode>, Iterator<SvgNode>>>()
            stack.addLast(nodes1.iterator() to nodes2.iterator())
            var firstTSpanNotEmitted = true
            while (stack.isNotEmpty()) {
                val (it1, it2) = stack.last()
                require(it1.hasNext() == it2.hasNext()) { "Node lists must have the same size." }
                if (!it1.hasNext()) {
                    stack.removeLast()
                    continue
                }
                val node1 = it1.next()
                val node2 = it2.next()
                val isFirstTSpan = firstTSpanNotEmitted && (node1 is SvgTSpanElement || node2 is SvgTSpanElement)

                action(node1, node2, isFirstTSpan)

                if (isFirstTSpan) firstTSpanNotEmitted = false
                stack.addLast(node1.children().iterator() to node2.children().iterator())
            }
        }
    }
}