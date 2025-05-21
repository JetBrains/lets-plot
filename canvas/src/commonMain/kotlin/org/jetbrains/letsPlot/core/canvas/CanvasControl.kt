/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.Canvas.Snapshot

interface CanvasControl : AnimationProvider, CanvasProvider, MouseEventSource, Dispatcher {
    val pixelDensity: Double

    val size: Vector

    fun addChild(canvas: Canvas)

    fun addChild(index: Int, canvas: Canvas)

    fun removeChild(canvas: Canvas)

    fun onResize(listener: (Vector) -> Unit): Registration

    fun snapshot(): Snapshot
}
