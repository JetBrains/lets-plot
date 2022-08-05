/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.svg

import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.Text.HorizontalAnchor
import jetbrains.datalore.plot.base.render.svg.Text.toTextAnchor
import jetbrains.datalore.vis.svg.SvgConstants
import jetbrains.datalore.vis.svg.SvgTextElement


class MultilineLabel(text: String) : SvgComponent() {
    private val myLines: List<SvgTextElement> = text.split('\n').map(::SvgTextElement)
    private var myTextColor: Color? = null
    private var myFontSize = 0.0
    private var myFontWeight: String? = null
    private var myFontFamily: String? = null
    private var myFontStyle: String? = null
    private var myLineHeight = 0.0

    init {
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

    fun y(): Double? {
        return myLines.firstOrNull()?.y()?.get()
    }

    fun setHorizontalAnchor(anchor: HorizontalAnchor) {
        myLines.forEach {
            it.setAttribute(SvgConstants.SVG_TEXT_ANCHOR_ATTRIBUTE, toTextAnchor(anchor))
        }
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
        myLines.forEach { it.x().set(x) }
    }

    fun setY(y: Double) {
        updatePositions(y)
    }

    fun setLineHeight(v: Double) {
        myLineHeight = v
        val yStart = y() ?: 0.0
        updatePositions(yStart)
    }

    private fun updatePositions(yStart: Double) {
        myLines.forEachIndexed { index, elem ->
            elem.y().set(yStart + myLineHeight * index)
        }
    }

    fun linesCount() = myLines.size
}