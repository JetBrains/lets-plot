/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Disposable
import kotlin.math.sign

class WheelZoomFeedback(
    private val onZoomed: (DoubleRectangle, InteractionTarget) -> Unit
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

                val viewport = calculateViewport(target.geomPlotRect, factor, zoomOrigin)
                target.setViewport(viewport)
                onZoomed(viewport, target)
            }
        )

        return object : Disposable {
            override fun dispose() {
                println("WheelZoomFeedback dispose.")
                interaction.dispose()
            }
        }
    }

    private fun calculateViewport(rect: DoubleRectangle, scaleFactor: Double, scaleOrigin: DoubleVector): DoubleRectangle {
        val newDim = rect.dimension.mul(scaleFactor)
        val newOrigin = rect.origin.add(scaleOrigin.subtract(rect.origin).mul(1 - scaleFactor))
        return DoubleRectangle(newOrigin, newDim)
    }
}