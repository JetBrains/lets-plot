/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.scene

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.letsPlot.raster.scene.Text.BaselineShift

internal class TSpan : Figure() {
    init {
        isMouseTransparent = false // see Element::isMouseTransparent for details
    }

    var text: String by variableAttr("")

    var baselineShift: BaselineShift by variableAttr(BaselineShift.NONE)
    var dy: Float by variableAttr(0f)
    var fontScale: Float by variableAttr(1f)

    var fontFamily: List<String> by variableAttr(emptyList())
    var fontStyle: FontStyle by variableAttr(FontStyle.NORMAL)
    var fontWeight: FontWeight by variableAttr(FontWeight.NORMAL)
    var fontSize: Float by variableAttr(Text.DEFAULT_FONT_SIZE)

    var layoutX: Float by variableAttr(0f)
    var layoutY: Float by variableAttr(0f)

    private val font by derivedAttr {
        val family = fontFamily.firstOrNull() ?: Font.DEFAULT_FAMILY
        Font(fontStyle, fontWeight, fontSize.toDouble() * fontScale, family)
    }

    val lineHeight by derivedAttr {
        font.fontSize
    }

    private val baseline by derivedAttr {
        -(baselineShift.percent * lineHeight)
    }

    private val styleData: StyleData by derivedAttr {
        StyleData(
            fillPaint = fillPaint(fill),
            strokePaint = strokePaint(stroke = stroke, strokeWidth = strokeWidth)
        )
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

    class StyleData(
        val fillPaint: Paint?,
        val strokePaint: Paint?,
    )

    override fun calculateLocalBBox(): DoubleRectangle {
        val peer = peer ?: return DoubleRectangle.ZERO
        if (text.isEmpty()) return DoubleRectangle.ZERO

        val textBBox = peer.measureText(text, font).let {
            DoubleRectangle.XYWH(
                x = it.bbox.left,
                y = it.bbox.top - baseline,
                width = it.bbox.width,
                height = it.bbox.height
            )
        }

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

    override fun onAttributeChanged(attrSpec: AttributeSpec) {
        if (
            attrSpec == TextAttrSpec ||
            attrSpec == FontAttrSpec ||
            attrSpec == BaselineShiftAttrSpec ||
            attrSpec == DyAttrSpec ||
            attrSpec == FontScaleAttrSpec
        ) {
            (parent as? Text)?.invalidateLayout()
        }
    }

    override fun repr(): String {
        return ", text: \"$text\"" + super.repr()
    }

    companion object {
        private val CLASS = ATTRIBUTE_REGISTRY.addClass(TSpan::class)

        val TextAttrSpec = CLASS.registerVariableAttr(TSpan::text, affectsBBox = true)
        val BaselineShiftAttrSpec = CLASS.registerVariableAttr(TSpan::baselineShift, affectsBBox = true)
        val DyAttrSpec = CLASS.registerVariableAttr(TSpan::dy, affectsBBox = true)
        val FontScaleAttrSpec = CLASS.registerVariableAttr(TSpan::fontScale, affectsBBox = true)

        val FontFamilyAttrSpec = CLASS.registerVariableAttr(TSpan::fontFamily, affectsBBox = true)
        val FontStyleAttrSpec = CLASS.registerVariableAttr(TSpan::fontStyle, affectsBBox = true)
        val FontWeightAttrSpec = CLASS.registerVariableAttr(TSpan::fontWeight, affectsBBox = true)
        val FontSizeAttrSpec = CLASS.registerVariableAttr(TSpan::fontSize, affectsBBox = true)

        val LayoutXAttrSpec = CLASS.registerVariableAttr(TSpan::layoutX, affectsBBox = true)
        val LayoutYAttrSpec = CLASS.registerVariableAttr(TSpan::layoutY, affectsBBox = true)

        val FontAttrSpec = CLASS.registerDerivedAttr(
            kProp = TSpan::font,
            dependencies = setOf(FontFamilyAttrSpec, FontWeightAttrSpec, FontStyleAttrSpec, FontSizeAttrSpec, FontScaleAttrSpec)
        )
        val LineHeightAttrSpec = CLASS.registerDerivedAttr(TSpan::lineHeight, dependencies = setOf(FontAttrSpec))
        val BaselineAttrSpec = CLASS.registerDerivedAttr(TSpan::baseline, dependencies = setOf(BaselineShiftAttrSpec, DyAttrSpec, LineHeightAttrSpec))
        val StyleDataAttrSpec = CLASS.registerDerivedAttr(TSpan::styleData, dependencies = setOf(FillAttrSpec, StrokeAttrSpec, StrokeWidthAttrSpec))
    }
}
