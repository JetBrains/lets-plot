/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.feedback

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.letsPlot.commons.debounce
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.interact.InteractionContext
import org.jetbrains.letsPlot.core.interact.InteractionUtil
import org.jetbrains.letsPlot.core.interact.ToolFeedback
import org.jetbrains.letsPlot.core.interact.event.ModifiersMatcher
import org.jetbrains.letsPlot.core.interact.mouse.MouseWheelInteraction
import kotlin.math.abs

class WheelZoomFeedback(
    private val modifiersMatcher: ModifiersMatcher,
    private val onCompleted: (targetId: String?, dataBounds: DoubleRectangle, scaleFactor: DoubleVector) -> Unit
) : ToolFeedback {
    override fun start(ctx: InteractionContext): Disposable {
        val interaction = MouseWheelInteraction(ctx, modifiersMatcher)
        var initialRange: DoubleVector? = null
        var completed = false

        // Accumulated state for the debounced onCompleted call.
        var lastTargetId: String? = null
        var lastDataBounds: DoubleRectangle? = null
        var lastScaleFactor: DoubleVector? = null

        val fireCompletedDebounced = debounce<Unit>(
            DEBOUNCE_DELAY_MS,
            CoroutineScope(Dispatchers.Default)
        ) {
            completed = true
            onCompleted(lastTargetId, lastDataBounds!!, lastScaleFactor!!)
        }

        interaction.loop(
            onZoomed = { (target, zoomOrigin, zoomDelta) ->
                if (completed) return@loop

                val zoomStep = if (abs(zoomDelta) > 0.3) {
                    // assume this is a wheel scroll - triggered less often, so we can use a fixed step
                    0.08
                } else {
                    // assume this is touchpad zoom - triggered more often, so decrease the step to prevent too fast zoom.
                    // Use zoomDelta to follow the gesture inertia.
                    abs(zoomDelta) / 10
                }

                val factor = if (zoomDelta < 0) {
                    1 - zoomStep // zoom in, reduce viewport
                } else {
                    1 + zoomStep // zoom out, enlarge viewport
                }

                val viewport = InteractionUtil.viewportFromScale(target.geomBounds, factor, zoomOrigin)

                val range = initialRange
                    ?: (target.dataBounds().dimension).also { initialRange = it }

                val (dataBounds, _) = target.applyViewport(viewport, ctx)

                lastTargetId = target.id
                lastDataBounds = dataBounds
                lastScaleFactor = DoubleVector(
                    range.x / dataBounds.dimension.x,
                    range.y / dataBounds.dimension.y
                )

                fireCompletedDebounced(Unit)
            }
        )

        return object : Disposable {
            override fun dispose() {
                interaction.dispose()
            }
        }
    }

    companion object {
        private const val DEBOUNCE_DELAY_MS = 30L
    }
}