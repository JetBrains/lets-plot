/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.canvascontrols

import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.livemap.LiveMap

class LiveMapContent(
    private val liveMap: LiveMap
) : CanvasContent {
    private var shown = false

    override fun show(parentControl: CanvasControl) {
        liveMap.draw(parentControl)
        shown = true
    }

    override fun hide() {
        if (shown) {
            liveMap.dispose()
            shown = false
        }
    }

    fun addErrorHandler(handler: (Throwable) -> Unit): Registration {
        return liveMap.addErrorHandler(handler)
    }
}