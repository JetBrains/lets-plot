/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.config

import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.property.PropertyChangeEvent
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.intern.observable.property.ValueProperty
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure2
import org.jetbrains.letsPlot.livemap.LiveMap
import org.jetbrains.letsPlot.livemap.canvascontrols.LiveMapPresenter

class LiveMapCanvasFigure(
    private val liveMap: LiveMap
) : CanvasFigure, CanvasFigure2 {
    private val myBounds = ValueProperty(Rectangle(0, 0, 0, 0))
    private val myLiveMapPresenter = LiveMapPresenter()
    private val repaintRequestListeners = mutableListOf<() -> Unit>()

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

        return Registration.from(
            myLiveMapPresenter,
            liveMap.addRepaintListener { requestRedraw() }
        )
    }

    override fun mapToCanvas(canvasPeer: CanvasPeer): Registration {
        return CompositeRegistration(
            liveMap.attachToCanvasPeer(canvasPeer, bounds().get().dimension),
            liveMap.addRepaintListener { requestRedraw() }
        )
    }

    override val size: Vector
        get() = myBounds.get().dimension

    override fun paint(context2d: Context2d) {
        liveMap.paint(context2d)
        context2d.save()
        context2d.setFillStyle(Color.GREEN)
        context2d.setFont(Font(fontSize = 46.0, fontFamily = "Noto Sans Mono"))
        context2d.fillText("LiveMapPresenter: unexpected direct paint call", 10.0, 60.0)
        context2d.restore()

    }

    override fun onRepaintRequested(listener: () -> Unit): Registration {
        repaintRequestListeners.add(listener)
        return Registration.onRemove { repaintRequestListeners.remove(listener) }
    }

    private fun requestRedraw() {
        repaintRequestListeners.forEach { it() }
    }

    override fun resize(width: Number, height: Number) {
        TODO("Not yet implemented")
    }

    override val mouseEventPeer: MouseEventPeer
        get() = TODO("Not yet implemented")

    override fun isReady(): Boolean {
        return !liveMap.isLoading.get()
    }

    override fun onReady(listener: () -> Unit): Registration {
        if (isReady()) {
            listener()
            return Registration.EMPTY
        } else {
            val isLoading = liveMap.isLoading
            return isLoading.addHandler(object : EventHandler<PropertyChangeEvent<out Boolean>> {
                override fun onEvent(event: PropertyChangeEvent<out Boolean>) {
                    println("LiveMapCanvasFigure: onReady: isLoading=${event.newValue}")
                    if (!isLoading.get()) {
                        listener()
                    }
                }
            })
        }
    }
}
