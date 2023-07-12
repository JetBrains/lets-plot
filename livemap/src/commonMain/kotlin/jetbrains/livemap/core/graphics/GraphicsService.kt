/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.graphics

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.Font
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.input.CursorStyle

interface GraphicsService {
    fun measure(text: String, font: Font): DoubleVector
    fun repaint()

    fun addToRenderer(obj: RenderBox)
    fun removeFromRenderer(obj: RenderBox)

    fun onClick(renderBox: RenderBox, onClick: () -> Unit): Registration
    fun setCursor(obj: RenderBox, cursorStyle: CursorStyle)
    fun defaultCursor(obj: RenderBox)
    fun addAnimation(animation: Animation): Registration
}
