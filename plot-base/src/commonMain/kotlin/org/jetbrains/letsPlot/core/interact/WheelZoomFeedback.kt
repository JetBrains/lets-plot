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
            onZoomed = {
                val zoomStep = 1.05
                val factor = when (it.zoomDelta.sign) {
                    -1.0 -> 1 / zoomStep // zoom in, reduce viewport
                    else -> zoomStep // zoom out, enlarge viewport
                }

                val viewport = scaleRect(it.target.geomBounds, factor, it.zoomOrigin)
                it.target.zoom(viewport)
                onZoomed(viewport, it.target)
            }
        )

        return object : Disposable {
            override fun dispose() {
                println("WheelZoomFeedback dispose.")
                interaction.dispose()
            }
        }
    }

    private fun scaleRect(rect: DoubleRectangle, factor: Double, origin: DoubleVector): DoubleRectangle {
        val newDim = rect.dimension.mul(factor)
        val originOffset = origin.subtract(rect.origin)
        val newOrigin = rect.origin.add(originOffset.mul(1 - factor))

        return DoubleRectangle(newOrigin, newDim)
    }
}