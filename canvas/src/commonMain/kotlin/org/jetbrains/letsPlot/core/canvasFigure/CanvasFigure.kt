/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvasFigure

import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.SomeFig
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvas.Context2d

interface CanvasFigure : SomeFig {
    fun bounds(): ReadableProperty<Rectangle>

    fun mapToCanvas(canvasControl: CanvasControl): Registration

}

interface CanvasFigure2 : SomeFig {
    // V2 API. Default impl. to not break existing implementations
    val size: Vector get() = Vector.ZERO
    fun paint(context2d: Context2d) {}
    fun onRepaintRequested(listener: () -> Unit): Registration = Registration.EMPTY
    fun resize(width: Number, height: Number) {}
    fun mapToCanvas(canvasPeer: CanvasPeer): Registration = Registration.EMPTY
    val eventPeer: MouseEventPeer get() = MouseEventPeer()
}
