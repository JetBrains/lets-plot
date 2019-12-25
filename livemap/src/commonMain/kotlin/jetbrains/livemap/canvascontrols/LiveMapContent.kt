/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvascontrols

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.EventSource
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.livemap.LiveMap

class LiveMapContent(
    private val liveMap: LiveMap
) : CanvasContent, EventSource<Throwable> {

    override fun show(parentControl: CanvasControl) {
        liveMap.draw(parentControl)
    }

    override fun hide() {
        liveMap.dispose()
    }

    override fun addHandler(handler: EventHandler<Throwable>): Registration {
        return liveMap.addHandler(handler)
    }

    fun addHandler(handler: (Throwable) -> Unit): Registration {
        return liveMap.addHandler(
            object : EventHandler<Throwable> {
                override fun onEvent(event: Throwable) {
                    handler(event)
                }
            }
        )
    }
}