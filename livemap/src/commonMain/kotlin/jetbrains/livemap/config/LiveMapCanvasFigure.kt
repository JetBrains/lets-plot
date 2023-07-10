/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.config

import org.jetbrains.letsPlot.commons.intern.async.Async
import jetbrains.datalore.base.geometry.Rectangle
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.intern.observable.property.ValueProperty
import org.jetbrains.letsPlot.commons.registration.Registration
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvasFigure.CanvasFigure
import jetbrains.livemap.LiveMap
import jetbrains.livemap.canvascontrols.LiveMapPresenter

class LiveMapCanvasFigure (private val liveMap: Async<LiveMap>) : CanvasFigure {
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
}
