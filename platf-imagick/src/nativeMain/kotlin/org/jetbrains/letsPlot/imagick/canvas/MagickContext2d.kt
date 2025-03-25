/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import MagickWand.*
import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Context2dDelegate
import org.jetbrains.letsPlot.core.canvas.Font

class MagickContext2d(
    private val wand: CPointer<MagickWand>?
) : Context2d by Context2dDelegate() {

    private val drawingWand = NewDrawingWand() ?: error { "Failed to create DrawingWand" }
    private val pixelWand = NewPixelWand() ?: error { "Failed to create PixelWand" }
    private val state = MagickState()

    override fun setFont(f: Font) {
        val size = f.fontSize
        val family = f.fontFamily
        val style = f.fontStyle
        val weight = f.fontWeight

        // Set font size
        DrawSetFontSize(drawingWand, size)
    }

    override fun setFillStyle(color: Color?) {
        state.current().fillColor = color?.toCssColor() ?: Color.BLACK.toCssColor()

        PixelSetColor(pixelWand, state.current().fillColor)
        DrawSetFillColor(drawingWand, pixelWand)
    }

    override fun setStrokeStyle(color: Color?) {
        state.current().strokeColor = color?.toCssColor() ?: Color.BLACK.toCssColor()

        PixelSetColor(pixelWand, state.current().fillColor)
        DrawSetStrokeColor(drawingWand, pixelWand)
    }

    override fun fillText(text: String, x: Double, y: Double) {
        memScoped {
            val textCStr = text.cstr.ptr.reinterpret<UByteVar>()
            DrawAnnotation(drawingWand, x, y, textCStr)
            MagickDrawImage(wand, drawingWand)
        }
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        memScoped {
            val textCStr = text.cstr.ptr.reinterpret<UByteVar>()
            DrawAnnotation(drawingWand, x, y, textCStr)
            MagickDrawImage(wand, drawingWand)
        }
    }

    override fun setLineWidth(lineWidth: Double) {
        println("setLineWidth: $lineWidth")
        DrawSetStrokeWidth(drawingWand, lineWidth)
    }

    override fun beginPath() {
        DrawPathStart(drawingWand)
    }

    override fun moveTo(x: Double, y: Double) {
        DrawPathMoveToAbsolute(drawingWand, x, y)
    }

    override fun lineTo(x: Double, y: Double) {
        DrawPathLineToAbsolute(drawingWand, x, y)
    }

    override fun closePath() {
        DrawPathClose(drawingWand)
    }

    override fun stroke() {
        DrawPathFinish(drawingWand)
        MagickDrawImage(wand, drawingWand)
    }

    override fun fill() {
        DrawPathFinish(drawingWand)
        MagickDrawImage(wand, drawingWand)
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        DrawRectangle(drawingWand, x, y, x + w, y + h)
        MagickDrawImage(wand, drawingWand)
    }

    override fun save() {
        state.push()
    }

    override fun restore() {
        state.pop()
        DrawAffine(drawingWand, state.current().transform.ptr)

        PixelSetColor(pixelWand, state.current().fillColor)
        DrawSetFillColor(drawingWand, pixelWand)

        PixelSetColor(pixelWand, state.current().strokeColor)
        DrawSetStrokeColor(drawingWand, pixelWand)

        //graphics.stroke = it.stroke
        //graphics.font = it.font
        //graphics.composite = AlphaComposite.getInstance(SRC_OVER, state.globalAlpha)

        //state.removeAt(state.lastIndex)
    }
}