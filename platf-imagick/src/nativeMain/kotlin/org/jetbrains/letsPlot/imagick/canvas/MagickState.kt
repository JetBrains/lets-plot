/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import MagickWand.*
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import org.jetbrains.letsPlot.commons.values.Color

class MagickState {
    class State {
        private val pixelWand = NewPixelWand() ?: error { "Failed to create PixelWand" }

        fun withStrokeWand(block: (CPointer<DrawingWand>) -> Unit) {
            val strokeWand = NewDrawingWand() ?: error { "DrawingWand was null" }

            PixelSetColor(pixelWand, strokeColor)
            DrawSetStrokeColor(strokeWand, pixelWand)
            DrawSetStrokeWidth(strokeWand, strokeWidth)

            PixelSetColor(pixelWand, Color.TRANSPARENT.toCssColor())
            DrawSetFillColor(strokeWand, pixelWand)

            block(strokeWand)

            DestroyDrawingWand(strokeWand)
        }

        fun withFillWand(block: (CPointer<DrawingWand>) -> Unit) {
            val fillWand = NewDrawingWand() ?: error { "DrawingWand was null" }

            PixelSetColor(pixelWand, fillColor)
            DrawSetFillColor(fillWand, pixelWand)

            PixelSetColor(pixelWand, Color.TRANSPARENT.toCssColor())
            DrawSetStrokeColor(fillWand, pixelWand)

            block(fillWand)

            DestroyDrawingWand(fillWand)
        }

        var strokeColor: String = Color.TRANSPARENT.toCssColor()
        var strokeWidth: Double = 1.0
        var fillColor: String = Color.TRANSPARENT.toCssColor()
        var transform: AffineMatrix = IDENTITY

        //var stroke: BasicStroke = BasicStroke(),
        //var textBaseline: TextBaseline = TextBaseline.ALPHABETIC,
        //var textAlign: TextAlign = TextAlign.START,
        //var font: AwtFont = AwtFont(AwtFont.SERIF, AwtFont.PLAIN, 10),
        //var globalAlpha: Float = 1f,

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


    private val stack = mutableListOf<State>(State())

    fun push() {
        stack.add(current())
    }

    fun pop() {
        stack.removeLast()
    }

    fun current(): State {
        return stack.last()
    }
}
