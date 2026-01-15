/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


package org.jetbrains.letsPlot.raster.scene

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight

internal class Text : Container() {
    var x: Float by variableAttr(0f)
    var y: Float by variableAttr(0f)
    var textOrigin: VerticalAlignment? by variableAttr(null)
    var textAlignment: HorizontalAlignment? by variableAttr(null)

    var stroke: Color? by variableAttr(null)
    var strokeWidth: Float by variableAttr(1f)
    var strokeOpacity: Float by variableAttr(1f)
    var strokeDashArray: List<Float>? by variableAttr(null)
    var strokeMiter: Float? by variableAttr(null)

    var fill: Color? by variableAttr(Color.BLACK)
    var fillOpacity: Float by variableAttr(1f)

    var fontFamily: List<String> by variableAttr(emptyList())
    var fontStyle: FontStyle by variableAttr(FontStyle.NORMAL)
    var fontWeight: FontWeight by variableAttr(FontWeight.NORMAL)
    var fontSize: Float by variableAttr(DEFAULT_FONT_SIZE)

    private var needLayout = true

    private val cx: Float by derivedAttr {
        fun contentWidth(): Double = children.fold(0.0) { overallWidth, node -> overallWidth + node.bBoxLocal.width }

        when (textAlignment) {
            HorizontalAlignment.LEFT -> 0.0
            HorizontalAlignment.CENTER -> -contentWidth() / 2.0
            HorizontalAlignment.RIGHT -> -contentWidth()
            null -> 0.00
        }.toFloat()
    }

    private val cy by derivedAttr {
        // Vertical alignment should be computed without sub/super script, that's why we don't use textBlobInfo here
        when (textOrigin) {
            VerticalAlignment.TOP -> fontSize * 0.74f
            VerticalAlignment.CENTER -> fontSize * 0.37f
            null -> 0.0f
        }
    }

    override fun render(ctx: Context2d) {
        if (needLayout) {
            layoutChildren()
        }
    }

    fun layoutChildren() {
        var curX = 0f
        var dy = 0f

        children.forEach { tSpan ->
            tSpan as TSpan

            dy += (tSpan.dy * tSpan.lineHeight).toFloat()

            tSpan.layoutX = x + cx + curX
            tSpan.layoutY = y + cy + dy

            curX += tSpan.bBoxLocal.width.toFloat()
        }

        needLayout = false
    }

    internal fun invalidateLayout() {
        needLayout = true
    }

    override fun onAttributeChanged(attrSpec: AttributeSpec) {
        if (attrSpec == XAttrSpec
            || attrSpec == YAttrSpec
            || attrSpec == TextAlignmentAttrSpec
            || attrSpec == TextOriginAttrSpec
        ) {
            invalidateLayout()
        }

        children.forEach { tSpan ->
            tSpan as TSpan
            // propagate Text attributes to TSpan children using Figure and TSpan attrSpecs
            when (attrSpec) {
                FillAttrSpec -> tSpan.inheritValue(Figure.FillAttrSpec, fill)
                StrokeAttrSpec -> tSpan.inheritValue(Figure.StrokeAttrSpec, stroke)
                StrokeWidthAttrSpec -> tSpan.inheritValue(Figure.StrokeWidthAttrSpec, strokeWidth)
                StrokeDashArrayAttrSpec -> tSpan.inheritValue(Figure.StrokeDashArrayAttrSpec, strokeDashArray)
                StrokeOpacityAttrSpec -> tSpan.inheritValue(Figure.StrokeOpacityAttrSpec, strokeOpacity)

                FontFamilyAttrSpec -> tSpan.inheritValue(TSpan.FontFamilyAttrSpec, fontFamily)
                FontSizeAttrSpec -> tSpan.inheritValue(TSpan.FontSizeAttrSpec, fontSize)
                FontStyleAttrSpec -> tSpan.inheritValue(TSpan.FontStyleAttrSpec, fontStyle)
                FontWeightAttrSpec -> tSpan.inheritValue(TSpan.FontWeightAttrSpec, fontWeight)
            }
        }
    }

    override fun onChildAdded(event: CollectionItemEvent<out Node>) {
        super.onChildAdded(event)

        val tSpan = event.newItem as TSpan
        tSpan.inheritValue(Figure.FillAttrSpec, fill)
        tSpan.inheritValue(Figure.StrokeAttrSpec, stroke)
        tSpan.inheritValue(Figure.StrokeWidthAttrSpec, strokeWidth)
        tSpan.inheritValue(Figure.StrokeOpacityAttrSpec, strokeOpacity)
        tSpan.inheritValue(Figure.StrokeDashArrayAttrSpec, strokeDashArray)
        tSpan.inheritValue(TSpan.FontFamilyAttrSpec, fontFamily)
        tSpan.inheritValue(TSpan.FontStyleAttrSpec, fontStyle)
        tSpan.inheritValue(TSpan.FontWeightAttrSpec, fontWeight)
        tSpan.inheritValue(TSpan.FontSizeAttrSpec, fontSize)

        invalidateLayout()
    }

    override fun calculateLocalBBox(): DoubleRectangle {
        if (needLayout) {
            layoutChildren()
        }

        return super.calculateLocalBBox()
    }

    enum class VerticalAlignment {
        TOP,
        CENTER
    }

    enum class HorizontalAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    enum class BaselineShift(
        val percent: Float
    ) {
        SUB(-0.4f),
        SUPER(0.4f),
        NONE(0f)
    }

    companion object {
        val CLASS = ATTRIBUTE_REGISTRY.addClass(Text::class)

        val XAttrSpec = CLASS.registerVariableAttr(Text::x, affectsBBox = true)
        val YAttrSpec = CLASS.registerVariableAttr(Text::y, affectsBBox = true)
        val TextOriginAttrSpec = CLASS.registerVariableAttr(Text::textOrigin, affectsBBox = true)
        val TextAlignmentAttrSpec = CLASS.registerVariableAttr(Text::textAlignment, affectsBBox = true)

        val FillAttrSpec = CLASS.registerVariableAttr(Text::fill)
        val FillOpacityAttrSpec = CLASS.registerVariableAttr(Text::fillOpacity)

        val StrokeAttrSpec = CLASS.registerVariableAttr(Text::stroke)
        val StrokeWidthAttrSpec = CLASS.registerVariableAttr(Text::strokeWidth, affectsBBox = true)
        val StrokeOpacityAttrSpec = CLASS.registerVariableAttr(Text::strokeOpacity)
        val StrokeDashArrayAttrSpec = CLASS.registerVariableAttr(Text::strokeDashArray)
        val StrokeMiterAttrSpec = CLASS.registerVariableAttr(Text::strokeMiter)

        val FontFamilyAttrSpec = CLASS.registerVariableAttr(Text::fontFamily)
        val FontStyleAttrSpec = CLASS.registerVariableAttr(Text::fontStyle)
        val FontWeightAttrSpec = CLASS.registerVariableAttr(Text::fontWeight)
        val FontSizeAttrSpec = CLASS.registerVariableAttr(Text::fontSize)

        val CxAttrSpec = CLASS.registerDerivedAttr(Text::cx, dependencies = setOf(TextAlignmentAttrSpec))
        val CyAttrSpec = CLASS.registerDerivedAttr(Text::cy, dependencies = setOf(TextOriginAttrSpec, FontSizeAttrSpec))

        const val DEFAULT_FONT_SIZE: Float = 16f
        val DEFAULT_FONT_FAMILY: List<String> = emptyList()
    }
}
