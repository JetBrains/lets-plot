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
) : Context2d by Context2dDelegate(true) {
    private val pixelWand = NewPixelWand() ?: error { "Failed to create PixelWand" }
    private var state = ContextState()

    private var currentPath: MagickPath = MagickPath()

    private data class ContextState(
        var strokeColor: String = Color.TRANSPARENT.toCssColor(),
        var strokeWidth: Double = 1.0,
        var dashPattern: DoubleArray = doubleArrayOf(),
        var fillColor: String = Color.TRANSPARENT.toCssColor(),
        var transform: AffineMatrix = IDENTITY,
    )

    private val contextStates = mutableListOf<ContextState>()

    override fun setFont(f: Font) {
        val size = f.fontSize
        val family = f.fontFamily
        val style = f.fontStyle
        val weight = f.fontWeight

        // Set font size
        //DrawSetFontSize(drawingWand, size)
    }

    override fun setFillStyle(color: Color?) {
        state.fillColor = color?.toCssColor() ?: Color.BLACK.toCssColor()
    }

    override fun setStrokeStyle(color: Color?) {
        state.strokeColor = color?.toCssColor() ?: Color.BLACK.toCssColor()
    }

    override fun setLineWidth(lineWidth: Double) {
        state.strokeWidth = lineWidth
    }

    override fun setLineDash(lineDash: DoubleArray) {
        state.dashPattern = lineDash
    }

    override fun fillText(text: String, x: Double, y: Double) {
        memScoped {
            withFillWand { fillWand ->
                val textCStr = text.cstr.ptr.reinterpret<UByteVar>()
                DrawAnnotation(fillWand, x, y, textCStr)
                MagickDrawImage(wand, fillWand)
            }
        }
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        memScoped {
            withStrokeWand { strokeWand ->
                val textCStr = text.cstr.ptr.reinterpret<UByteVar>()
                DrawAnnotation(strokeWand, x, y, textCStr)
                MagickDrawImage(wand, strokeWand)
            }
        }
    }

    override fun beginPath() {
        currentPath = MagickPath()
    }

    override fun moveTo(x: Double, y: Double) {
        currentPath.moveTo(x, y)
    }

    override fun lineTo(x: Double, y: Double) {
        currentPath.lineTo(x, y)
    }

    override fun arc(
        x: Double,
        y: Double,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean
    ) {
        currentPath.arc(x, y, radius, startAngle, endAngle, anticlockwise)
    }

    override fun closePath() {
        currentPath.closePath()
    }

    override fun stroke() {
        withStrokeWand { strokeWand ->
            currentPath.draw(strokeWand)
            DrawPathFinish(strokeWand)
            MagickDrawImage(wand, strokeWand)
        }
    }

    override fun fill() {
        withFillWand { fillWand ->
            currentPath.draw(fillWand)
            DrawPathFinish(fillWand)
            MagickDrawImage(wand, fillWand)
        }
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        withFillWand { fillWand ->
            DrawRectangle(fillWand, x, y, x + w, y + h)
            MagickDrawImage(wand, fillWand)
        }
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        withStrokeWand { strokeWand ->
            DrawRectangle(strokeWand, x, y, x + w, y + h)
            MagickDrawImage(wand, strokeWand)
        }
    }

    override fun save() {
        contextStates += state
        state = state.copy()
    }

    override fun restore() {
        contextStates.removeLastOrNull()
        state = contextStates.lastOrNull() ?: ContextState()
    }

    fun withStrokeWand(block: (CPointer<DrawingWand>) -> Unit) {
        val strokeWand = NewDrawingWand() ?: error { "DrawingWand was null" }

        PixelSetColor(pixelWand, state.strokeColor)
        DrawSetStrokeColor(strokeWand, pixelWand)
        DrawSetStrokeWidth(strokeWand, state.strokeWidth)

        memScoped {
            val pattern = allocArray<DoubleVar>(state.dashPattern.size)
            state.dashPattern.forEachIndexed { index, value -> pattern[index] = value }
            DrawSetStrokeDashArray(strokeWand, state.dashPattern.size.toULong(), pattern)
        }

        PixelSetColor(pixelWand, Color.TRANSPARENT.toCssColor())
        DrawSetFillColor(strokeWand, pixelWand)

        block(strokeWand)

        DestroyDrawingWand(strokeWand)
    }

    fun withFillWand(block: (CPointer<DrawingWand>) -> Unit) {
        val fillWand = NewDrawingWand() ?: error { "DrawingWand was null" }

        PixelSetColor(pixelWand, state.fillColor)
        DrawSetFillColor(fillWand, pixelWand)

        PixelSetColor(pixelWand, Color.TRANSPARENT.toCssColor())
        DrawSetStrokeColor(fillWand, pixelWand)

        block(fillWand)

        DestroyDrawingWand(fillWand)
    }

    companion object {
        val IDENTITY = nativeHeap.alloc<AffineMatrix>().apply {
            sx = 1.0  // Scale X (no change)
            sy = 1.0  // Scale Y (no change)
            rx = 0.0  // Shear X
            ry = 0.0  // Shear Y
            tx = 0.0 // Translate X
            ty = 0.0  // Translate Y
        }
    }

}
