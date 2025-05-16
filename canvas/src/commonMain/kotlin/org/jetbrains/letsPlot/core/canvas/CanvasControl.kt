/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.geometry.Vector

interface CanvasControl : AnimationProvider, CanvasProvider, MouseEventSource, Dispatcher {
    val pixelDensity: Double

    val size: Vector

    fun addChild(canvas: Canvas)

    fun addChild(index: Int, canvas: Canvas)

    fun removeChild(canvas: Canvas)
}
