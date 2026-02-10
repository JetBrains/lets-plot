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

interface Drawable {
    fun bounds(): ReadableProperty<Rectangle>

    fun mapToCanvas(canvasControl: CanvasControl): Registration

}

interface Drawable2 : AsyncRenderer {
    // V2 API. Default impl. to not break existing implementations
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
