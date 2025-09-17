/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.config

import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.intern.observable.property.ValueProperty
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure
import org.jetbrains.letsPlot.livemap.LiveMap
import org.jetbrains.letsPlot.livemap.canvascontrols.LiveMapPresenter

class LiveMapCanvasFigure (private val liveMap: LiveMap) : CanvasFigure {
    private val myBounds = ValueProperty(Rectangle(0, 0, 0, 0))
    private val myLiveMapPresenter = LiveMapPresenter()

    val isLoading: ReadableProperty<out Boolean>
        get() = myLiveMapPresenter.isLoading

    fun setBounds(bounds: Rectangle) {
        myBounds.set(bounds)
    }

    override fun bounds(): ReadableProperty<Rectangle> {
        return myBounds
    }

    override fun mapToCanvas(canvasControl: CanvasControl): Registration {
        myLiveMapPresenter.render(canvasControl, liveMap)

        return Registration.from(myLiveMapPresenter)
    }

    override val size: Vector
        get() = TODO("Not yet implemented")

    override fun paint(context2d: Context2d) {
        TODO("Not yet implemented")
    }

    override fun onRepaintRequested(listener: () -> Unit): Registration {
        TODO("Not yet implemented")
    }

    override fun resize(width: Number, height: Number) {
        TODO("Not yet implemented")
    }

    override fun mapToCanvas(canvasPeer: CanvasPeer): Registration {
        TODO("Not yet implemented")
    }

    override val eventPeer: MouseEventPeer
        get() = TODO("Not yet implemented")
}
