/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.config

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvasFigure.CanvasFigure
import jetbrains.livemap.LiveMap
import jetbrains.livemap.canvascontrols.LiveMapPresenter

class LiveMapCanvasFigure (private val liveMap: Async<LiveMap>) : CanvasFigure {
    private val myBounds = ValueProperty(DoubleRectangle(0.0, 0.0, 0.0, 0.0))
    private val myLiveMapPresenter = LiveMapPresenter()

    val isLoading: ReadableProperty<out Boolean>
        get() = myLiveMapPresenter.isLoading

    fun setBounds(bounds: DoubleRectangle) {
        myBounds.set(bounds)
    }

    override fun bounds(): ReadableProperty<DoubleRectangle> {
        return myBounds
    }

    override fun mapToCanvas(canvasControl: CanvasControl): Registration {
        myLiveMapPresenter.render(canvasControl, liveMap)

        return Registration.from(myLiveMapPresenter)
    }
}
