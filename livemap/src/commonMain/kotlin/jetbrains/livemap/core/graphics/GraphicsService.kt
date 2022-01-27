/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.graphics

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.input.CursorStyle

interface GraphicsService {
    fun measure(text: String, font: Context2d.Font): DoubleVector
    fun repaint()

    fun addToRenderer(obj: RenderBox)
    fun removeFromRenderer(obj: RenderBox)

    fun onClick(renderBox: RenderBox, onClick: () -> Unit): Registration
    fun setCursor(obj: RenderBox, cursorStyle: CursorStyle)
    fun defaultCursor(obj: RenderBox)
    fun addAnimation(animation: Animation): Registration
}
