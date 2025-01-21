/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.letsPlot.raster.mapping.svg.TextMeasurer
import org.jetbrains.letsPlot.raster.shape.Text.BaselineShift
import kotlin.reflect.KProperty

internal class TSpan(
    private val textMeasurer: TextMeasurer
) : Figure() {
    init {
        isMouseTransparent = false // see Element::isMouseTransparent for details
    }

    var text: String by visualProp("")

    var baselineShift: BaselineShift by visualProp(BaselineShift.NONE)
    var dy: Float by visualProp(0f)
    var fontScale: Float by visualProp(1f)

    var fontFamily: List<String> by visualProp(listOf(Font.DEFAULT_FAMILY))
    var fontStyle: FontStyle by visualProp(FontStyle.NORMAL)
    var fontWeight: FontWeight by visualProp(FontWeight.NORMAL)
    var fontSize by visualProp(Text.DEFAULT_FONT_SIZE)

    var layoutX: Float by visualProp(0f)
    var layoutY: Float by visualProp(0f)

    private val font by computedProp(TSpan::fontFamily, TSpan::fontWeight, TSpan::fontStyle, TSpan::fontSize) {
        val family = fontFamily.firstOrNull() ?: Font.DEFAULT_FAMILY
        Font(fontStyle, fontWeight, fontSize.toDouble(), family)
    }

    private val lineHeight by computedProp(TSpan::font) {
        font.fontSize//font.metrics.descent - font.metrics.ascent
    }

    private val styleData: StyleData by computedProp(
        Figure::fill,
        Figure::stroke,
        Figure::strokeWidth
    ) {
        StyleData(
            fillPaint = fillPaint(fill),
            strokePaint = strokePaint(stroke = stroke, strokeWidth = strokeWidth)
        )
    }

    val dimension by computedProp(TSpan::text, TSpan::font, TSpan::fontScale) {
        val width = textMeasurer.measureTextWidth(text, font)//textData?.width ?: 0.0
        val height = fontSize * fontScale
        DoubleVector(width, height)
    }

    val bbox by computedProp(TSpan::text, TSpan::font, TSpan::fontScale) {
        println("Fix TODO in TSpan")
        textMeasurer.measureText(text, font).let {
            DoubleRectangle.XYWH(
                x = 0.0,
                y = /*it.top*/ -16.0 - baselineShift.percent * lineHeight, // TODO: make sure it.bbox.top gives the same result as in AWT/Skia/JFX
                width = it.bbox.width * fontScale,
                height = it.bbox.height * fontScale
            )
        }
    }

    override fun render(canvas: Canvas) {
        canvas.context2d.setFont(font)
        canvas.context2d.scale(fontScale.toDouble())

        val x = (layoutX.toDouble() + bbox.left) / fontScale
        val y = (layoutY.toDouble() + lineHeight + bbox.top + lineHeight * dy) / fontScale

        styleData.fillPaint?.let {
            applyPaint(it, canvas)
            canvas.context2d.fillText(text, x, y)
        }

        styleData.strokePaint?.let {
            applyPaint(it, canvas)
            canvas.context2d.strokeText(text, x, y)
        }
    }

    private class StyleData(
        val fillPaint: Paint?,
        val strokePaint: Paint?,
    )

    override val localBounds: DoubleRectangle
        get() {
            val left = bbox.left
            val top = bbox.top
            val right = bbox.right
            val bottom = bbox.bottom

            return DoubleRectangle.LTRB(
                layoutX + left,
                layoutY + top,
                layoutX + right,
                layoutY + bottom
            )
        }

    override fun onPropertyChanged(prop: KProperty<*>) {
        if (
            prop == TSpan::text ||
            prop == TSpan::font ||
            prop == TSpan::baselineShift ||
            prop == TSpan::dy ||
            prop == TSpan::fontScale
        ) {
            (parent as? Text)?.invalidateLayout()
        }
    }

    override fun repr(): String? {
        return ", text: \"$text\"" + super.repr()
    }
}
