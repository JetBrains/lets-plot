/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Disposable

class WheelZoomFeedback(
    private val onZoomed: (DoubleRectangle, InteractionTarget) -> Unit
) : ToolFeedback {
    fun start(ctx: InteractionContext): Disposable {
        val interaction = MouseWheelInteraction(ctx)

        interaction.loop(
            onZoomed = {
                val target = it.target

                val zoomStep = 1.05

                val zoomFactor = if (it.zoomDelta < 0) zoomStep else 1 / zoomStep
                val localBounds = target.geomBounds.subtract(target.geomBounds.origin)
                val localPointer = it.zoomLocation.subtract(target.geomBounds.origin)

                val newBounds = zoom(zoomFactor, localPointer, localBounds)

                target.zoom(newBounds.origin.mul(1 / zoomFactor), DoubleVector(zoomFactor, zoomFactor))
                onZoomed(newBounds, target)
            }
        )

        return object : Disposable {
            override fun dispose() {
                println("WheelZoomFeedback dispose.")
                interaction.dispose()
            }
        }
    }

    fun zoom(zoomFactor: Double, mouse: DoubleVector, viewport: DoubleRectangle): DoubleRectangle {
        // Calculate the new width and height
        val newDim = viewport.dimension.mul(zoomFactor)
        val newOrigin = viewport.origin.add(mouse.subtract(viewport.origin).mul(1 - zoomFactor))

        return DoubleRectangle(newOrigin, newDim)
    }
}