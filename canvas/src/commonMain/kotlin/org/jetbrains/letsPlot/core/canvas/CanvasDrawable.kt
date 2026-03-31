/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.registration.Registration

interface CanvasDrawable: AsyncRenderer {
    @Deprecated("Use size property instead")
    fun bounds(): ReadableProperty<Rectangle> = error("CanvasDrawable.bounds() is deprecated, use size property instead")

    @Deprecated("Use mapToCanvas(canvasPeer: CanvasPeer) instead")
    fun mapToCanvas(canvasControl: CanvasControl): Registration = error("CanvasDrawable.mapToCanvas(canvasControl: CanvasControl) is deprecated, use mapToCanvas(canvasPeer: CanvasPeer) instead")

    // V2 API
    val size: Vector
    val mouseEventPeer: MouseEventPeer

    fun paint(context2d: Context2d)
    fun onRepaintRequested(listener: () -> Unit): Registration
    fun resize(width: Number, height: Number)
    fun mapToCanvas(canvasPeer: CanvasPeer): Registration
}

interface AsyncRenderer {
    fun isReady(): Boolean
    fun onReady(listener: () -> Unit): Registration
    fun onFrame(millisTime: Long)
}
