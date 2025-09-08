/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.commons.intern.util.TextWidthEstimator
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.HorizontalAnchor
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.toDY
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.toTextAnchor
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.core.plot.base.theme.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_STYLE_ATTRIBUTE
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.math.roundToInt

class TextLabel(private val text: String, private val markdown: Boolean = false) : SvgComponent() {
    private var myText: SvgTextElement = SvgTextElement()
    private var myClassName: String? = null
    private var myTextColor: Color? = null
    private var myFontSize = 0.0
    private var myFontWeight: String? = null
    private var myFontFamily: String? = null
    private var myFontStyle: String? = null
    private var myHorizontalAnchor: HorizontalAnchor = RichText.DEF_HORIZONTAL_ANCHOR
    private var myVerticalAnchor: Text.VerticalAnchor? = null

    init {
        resetText()
    }

    override fun buildComponent() {
    }

    override fun addClassName(className: String) = updateAndReset { myClassName = className }

    fun textColor(): WritableProperty<Color?> {
        return object : WritableProperty<Color?> {
            override fun set(value: Color?) = updateAndReset {
                // duplicate in 'style' to override styles of container
                myTextColor = value
            }
        }
    }

    fun textOpacity(): WritableProperty<Double?> {
        return myText.fillOpacity()
    }

    fun x(): Property<Double?> {
        return myText.x()
    }

    fun y(): Property<Double?> {
        return myText.y()
    }

    fun setHorizontalAnchor(anchor: HorizontalAnchor) = updateAndReset { myHorizontalAnchor = anchor }

    fun setVerticalAnchor(anchor: Text.VerticalAnchor) = updateAndReset { myVerticalAnchor = anchor }

    fun setFontSize(px: Double) = updateAndReset { myFontSize = px }

    /**
     * @param cssName : normal, bold, bolder, lighter
     */
    fun setFontWeight(cssName: String?) = updateAndReset { myFontWeight = cssName }

    /**
     * @param cssName : normal, italic, oblique
     */
    fun setFontStyle(cssName: String?) = updateAndReset { myFontStyle = cssName }

    /**
     * @param fontFamily : for example 'sans-serif' or 'Times New Roman'
     */
    fun setFontFamily(fontFamily: String?) = updateAndReset { myFontFamily = fontFamily }

    private inline fun <T> updateAndReset(setter: () -> T) {
        setter()
        resetText()
    }

    // Similar to MultilineLabel#resetLines()
    private fun resetText() {
        rootGroup.children().clear()
        updateText()
        rootGroup.children().add(myText)
    }

    // Similar to MultilineLabel#constructLines()
    private fun updateText() {
        val font = Font(
            family = DefaultFontFamilyRegistry().get(myFontFamily ?: FontFamily.DEF_FAMILY_NAME),
            size = myFontSize.roundToInt(),
            isBold = myFontWeight == "bold",
            isItalic = myFontStyle == "italic"
        )
        val styleAttr = Text.buildStyle(
            myTextColor,
            myFontSize,
            myFontWeight,
            myFontFamily,
            myFontStyle
        )
        val textElement = RichText.toSvg(
            text.replace("\n", " "), // TextLabel is a single-line text element
            font,
            markdown = markdown,
            anchor = myHorizontalAnchor
        ).firstOrNull() ?: SvgTextElement()
        val horizontalAnchor = determineHorizontalAnchor(textElement)
        myText = textElement
            .let { updateTextAttributes(styleAttr)(it) }
            .let { updateAnchors(horizontalAnchor)(it) }
    }

    // Similar to MultilineLabel#getActualHorizontalAnchor()
    private fun determineHorizontalAnchor(textElement: SvgTextElement): HorizontalAnchor {
        val x = MultilineLabel.getFirstTSpanChild(textElement)?.x()?.get()
        return when (x) {
            null -> myHorizontalAnchor
            else -> HorizontalAnchor.LEFT
        }
    }

    // Similar to MultilineLabel#updateLinesAttributes()
    private fun updateTextAttributes(styleAttr: String): (SvgTextElement) -> SvgTextElement {
        return { textElement ->
            textElement.setAttribute(SVG_STYLE_ATTRIBUTE, styleAttr)
            myClassName?.let { textElement.addClass(it) }
            myTextColor?.let { textElement.fillColor() } // set attribute for svg->canvas mapping to work
            textElement
        }
    }

    // Similar to MultilineLabel#updateAnchors()
    private fun updateAnchors(horizontalAnchor: HorizontalAnchor): (SvgTextElement) -> SvgTextElement {
        return { textElement ->
            textElement.setAttribute(SvgConstants.SVG_TEXT_ANCHOR_ATTRIBUTE, toTextAnchor(horizontalAnchor))
            // replace "dominant-baseline" with "dy" because "dominant-baseline" is not supported by Batik
            //    myText.setAttribute("dominant-baseline", toDominantBaseline(anchor));
            myVerticalAnchor?.let { textElement.setAttribute(SvgConstants.SVG_TEXT_DY_ATTRIBUTE, toDY(it)) }
            textElement
        }
    }
}
