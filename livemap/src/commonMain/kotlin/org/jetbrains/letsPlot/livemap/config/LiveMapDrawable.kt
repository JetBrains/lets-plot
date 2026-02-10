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
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.livemap.LiveMap
import org.jetbrains.letsPlot.livemap.canvascontrols.LiveMapPresenter

class LiveMapDrawable(
    private val liveMap: LiveMap
) : Drawable, Drawable2 {
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

    override val mouseEventPeer: MouseEventPeer = liveMap.mouseEventPeer

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
                    if (!isLoading.get()) {
                        listener()
                    }
                }
            })
        }
    }

    override fun onFrame(millisTime: Long) {
        liveMap.onFrame(millisTime)
    }
}
