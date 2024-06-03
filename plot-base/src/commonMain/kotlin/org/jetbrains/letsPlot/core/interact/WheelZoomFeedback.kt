/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.registration.Disposable
import kotlin.math.sign

class WheelZoomFeedback(
    private val onCompleted: (DoubleRectangle) -> Unit
) : ToolFeedback {
    fun start(ctx: InteractionContext): Disposable {
        val interaction = MouseWheelInteraction(ctx)

        interaction.loop(
            onZoomed = { (target, zoomOrigin, zoomDelta) ->
                val zoomStep = 1.05
                val factor = when (zoomDelta.sign) {
                    -1.0 -> 1 / zoomStep // zoom in, reduce viewport
                    else -> zoomStep // zoom out, enlarge viewport
                }

                val viewport = InteractionUtil.scaleViewport(target.geomBounds, factor, zoomOrigin)
                val dataBounds = target.applyViewport(viewport)
                onCompleted(dataBounds)
            }
        )

        return object : Disposable {
            override fun dispose() {
                println("WheelZoomFeedback dispose.")
                interaction.dispose()
            }
        }
    }
}