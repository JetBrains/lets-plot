/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.registration.Disposable
import kotlin.math.abs

class WheelZoomFeedback(
    private val onCompleted: (DoubleRectangle) -> Unit
) : ToolFeedback {
    fun start(ctx: InteractionContext): Disposable {
        val interaction = MouseWheelInteraction(ctx)

        interaction.loop(
            onZoomed = { (target, zoomOrigin, zoomDelta) ->
                val zoomStep = if (abs(zoomDelta) > 0.3) {
                    // assume this is a wheel scroll - triggered less often, so we can use a fixed step
                    0.08
                } else {
                    // assume this is a touchpad zoom - triggered more often, so decrease the step to prevent too fast zoom.
                    // Use zoomDelta to follow gesture inertia.
                    abs(zoomDelta) / 10
                }

                val factor = if (zoomDelta < 0) {
                    1 - zoomStep // zoom in, reduce viewport
                } else {
                    1 + zoomStep // zoom out, enlarge viewport
                }

                val viewport = InteractionUtil.viewportFromScale(target.geomBounds, factor, zoomOrigin)
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