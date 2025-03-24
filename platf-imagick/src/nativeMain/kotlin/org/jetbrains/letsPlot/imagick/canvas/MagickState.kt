/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import MagickWand.AffineMatrix
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import org.jetbrains.letsPlot.commons.values.Color

class MagickState {
    data class State(
        var strokeColor: String = Color.BLACK.toHexColor(),
        var fillColor: String = Color.BLACK.toHexColor(),
        //var stroke: BasicStroke = BasicStroke(),
        //var textBaseline: TextBaseline = TextBaseline.ALPHABETIC,
        //var textAlign: TextAlign = TextAlign.START,
        //var font: AwtFont = AwtFont(AwtFont.SERIF, AwtFont.PLAIN, 10),
        //var globalAlpha: Float = 1f,
        var transform: AffineMatrix = IDENTITY
    ) {
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