/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.letsPlot.raster.shape.Text.BaselineShift
import kotlin.reflect.KProperty

internal class TSpan : Figure() {
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

    private val font by computedProp(TSpan::fontFamily, TSpan::fontWeight, TSpan::fontStyle, TSpan::fontSize, TSpan::fontScale) {
        val family = fontFamily.firstOrNull() ?: Font.DEFAULT_FAMILY
        Font(fontStyle, fontWeight, fontSize.toDouble() * fontScale, family)
    }

    val lineHeight by computedProp(TSpan::font) {
        font.fontSize
    }

    private val baseline by computedProp(TSpan::baselineShift, TSpan::dy, TSpan::lineHeight) {
        -(baselineShift.percent * lineHeight)
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

    // TODO: replace with bbox as it reports the same size
    val dimension by computedProp(TSpan::text, TSpan::font, Element::peer) {
        val peer = peer ?: return@computedProp DoubleVector(0.0, 0.0)

        val textMeasure = peer.measureText(text, font)

        DoubleVector(
            textMeasure.bbox.width,
            textMeasure.bbox.height
        )
    }

    val textBBox by computedProp(TSpan::text, TSpan::font, TSpan::baseline, Element::peer) {
        val peer = peer ?: return@computedProp DoubleRectangle.ZERO
        if (text.isEmpty()) return@computedProp DoubleRectangle.XYWH(0.0, 0.0, 0.0, 0.0)

        peer.measureText(text, font).let {
            DoubleRectangle.XYWH(
                x = it.bbox.left,
                y = it.bbox.top - baseline,
                width = it.bbox.width,
                height = it.bbox.height
            )
        }
    }

    override fun render(ctx: Context2d) {
        ctx.setFont(font)

        val x = (layoutX.toDouble())
        val y = layoutY.toDouble() + baseline

        styleData.fillPaint?.let {
            applyPaint(it, ctx)
            ctx.fillText(text, x, y)
        }

        styleData.strokePaint?.let {
            applyPaint(it, ctx)
            ctx.strokeText(text, x, y)
        }
    }

    private class StyleData(
        val fillPaint: Paint?,
        val strokePaint: Paint?,
    )

    override val bBox: DoubleRectangle
        get() {
            val left = textBBox.left
            val top = textBBox.top
            val right = textBBox.right
            val bottom = textBBox.bottom

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
