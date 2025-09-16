/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.feedback

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.interact.InteractionContext
import org.jetbrains.letsPlot.core.interact.InteractionUtil
import org.jetbrains.letsPlot.core.interact.ToolFeedback
import org.jetbrains.letsPlot.core.interact.mouse.MouseWheelInteraction
import kotlin.math.abs

class WheelZoomFeedback(
    private val onCompleted: (targetId: String?, dataBounds: DoubleRectangle, scaleFactor: DoubleVector) -> Unit
) : ToolFeedback {
    override fun start(ctx: InteractionContext): Disposable {
        val interaction = MouseWheelInteraction(ctx)
        var initialRange: DoubleVector? = null

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

                val currentBounds = target.dataBounds()

                if (initialRange == null) {
                    initialRange = currentBounds.dimension
                }

                val (dataBounds, _) = target.applyViewport(viewport, ctx)

                val scaleFactor = initialRange?.let {
                    DoubleVector(
                        it.x / dataBounds.dimension.x,
                        it.y / dataBounds.dimension.y
                    )
                } ?: DoubleVector(1.0, 1.0)

                onCompleted(target.id, dataBounds, scaleFactor)
            }
        )

        return object : Disposable {
            override fun dispose() {
                interaction.dispose()
            }
        }
    }
}