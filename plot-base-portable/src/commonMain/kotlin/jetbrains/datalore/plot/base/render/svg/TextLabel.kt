/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.svg.SvgConstants
import jetbrains.datalore.vis.svg.SvgConstants.SVG_STYLE_ATTRIBUTE
import jetbrains.datalore.vis.svg.SvgConstants.SVG_TEXT_ANCHOR_END
import jetbrains.datalore.vis.svg.SvgConstants.SVG_TEXT_ANCHOR_MIDDLE
import jetbrains.datalore.vis.svg.SvgConstants.SVG_TEXT_DY_CENTER
import jetbrains.datalore.vis.svg.SvgConstants.SVG_TEXT_DY_TOP
import jetbrains.datalore.vis.svg.SvgTextElement

class TextLabel(text: String) : SvgComponent() {
    private val myText: SvgTextElement = SvgTextElement(text)
    private var myTextColor: Color? = null
    private var myFontSize = 0.0
    private var myFontWeight: String? = null
    private var myFontFamily: String? = null
    private var myFontStyle: String? = null

    init {
        rootGroup.children().add(myText)
    }

    override fun buildComponent() {

    }

    fun textColor(): WritableProperty<Color?> {
        return object : WritableProperty<Color?> {
            override fun set(value: Color?) {
                // set attribute for svg->canvas mapping to work
                myText.fillColor()

                // duplicate in 'style' to override styles of container
                myTextColor = value
                updateStyleAttribute()
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

    fun setHorizontalAnchor(anchor: HorizontalAnchor) {
        myText.setAttribute(SvgConstants.SVG_TEXT_ANCHOR_ATTRIBUTE, toTextAnchor(anchor))
    }

    fun setVerticalAnchor(anchor: VerticalAnchor) {
        // replace "dominant-baseline" with "dy" because "dominant-baseline" is not supported by Batik
        //    myText.setAttribute("dominant-baseline", toDominantBaseline(anchor));
        myText.setAttribute(SvgConstants.SVG_TEXT_DY_ATTRIBUTE, toDY(anchor))
    }

    fun setFontSize(px: Double) {
        myFontSize = px
        updateStyleAttribute()
    }

    /**
     * @param cssName : normal, bold, bolder, lighter
     */
    fun setFontWeight(cssName: String?) {
        myFontWeight = cssName
        updateStyleAttribute()
    }

    /**
     * @param cssName : normal, italic, oblique
     */
    fun setFontStyle(cssName: String?) {
        myFontStyle = cssName
        updateStyleAttribute()
    }

    /**
     * @param fontFamily : for example 'sans-serif' or 'Times New Roman'
     */
    fun setFontFamily(fontFamily: String?) {
        myFontFamily = fontFamily
        updateStyleAttribute()
    }

    private fun updateStyleAttribute() {
        val sb = StringBuilder()
        if (myTextColor != null) {
            sb.append("fill:").append(myTextColor!!.toHexColor()).append(';')
        }

        if (myFontSize > 0 && myFontFamily != null) {
            // use font shorthand because this format is expected by svg -> canvas mapper
            // font: [style] [weight] size family;
            val fnt = StringBuilder()
            if (!myFontStyle.isNullOrEmpty()) {
                fnt.append(myFontStyle!!).append(' ')
            }
            if (!myFontWeight.isNullOrEmpty()) {
                fnt.append(myFontWeight!!).append(' ')
            }
            fnt.append(myFontSize).append("px ")
            fnt.append(myFontFamily!!).append(";")

            sb.append("font:").append(fnt)
        } else {
            // set each property separately
            if (!myFontStyle.isNullOrBlank()) {
                sb.append("font-style:").append(myFontStyle!!).append(';')
            }
            if (!myFontWeight.isNullOrEmpty()) {
                sb.append("font-weight:").append(myFontWeight!!).append(';')
            }
            if (myFontSize > 0) {
                sb.append("font-size:").append(myFontSize).append("px;")
            }
            if (!myFontFamily.isNullOrEmpty()) {
                sb.append("font-family:").append(myFontFamily!!).append(';')
            }
        }

        myText.setAttribute(SVG_STYLE_ATTRIBUTE, sb.toString())
    }

    private fun toTextAnchor(anchor: HorizontalAnchor): String? {
        return when (anchor) {
            HorizontalAnchor.LEFT -> null // default - "start";
            HorizontalAnchor.MIDDLE -> SVG_TEXT_ANCHOR_MIDDLE
            HorizontalAnchor.RIGHT -> SVG_TEXT_ANCHOR_END
        }
    }

    private fun toDominantBaseline(anchor: VerticalAnchor): String? {
        return when (anchor) {
            VerticalAnchor.TOP -> "hanging"
            VerticalAnchor.CENTER -> "central"
            VerticalAnchor.BOTTOM -> null // default - "alphabetic";
        }
    }

    private fun toDY(anchor: VerticalAnchor): String? {
        return when (anchor) {
            VerticalAnchor.TOP -> SVG_TEXT_DY_TOP
            VerticalAnchor.CENTER -> SVG_TEXT_DY_CENTER
            VerticalAnchor.BOTTOM -> null // default
        }
    }

    enum class HorizontalAnchor {
        LEFT, RIGHT, MIDDLE
    }

    enum class VerticalAnchor {
        TOP, BOTTOM, CENTER
    }

}
