/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvascontrols

import org.jetbrains.letsPlot.commons.registration.Registration
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.livemap.LiveMap

class LiveMapContent(
    private val liveMap: LiveMap
) : CanvasContent {

    override fun show(parentControl: CanvasControl) {
        liveMap.draw(parentControl)
    }

    override fun hide() {
        liveMap.dispose()
    }

    fun addErrorHandler(handler: (Throwable) -> Unit): Registration {
        return liveMap.addErrorHandler(handler)
    }
}