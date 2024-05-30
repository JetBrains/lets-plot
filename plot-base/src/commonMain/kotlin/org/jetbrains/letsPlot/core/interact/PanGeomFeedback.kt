/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Disposable

class PanGeomFeedback(
    private val onCompleted:((DoubleVector, InteractionTarget) -> Unit) = { _, _ -> println("PanGeomFeedback complete.") },
) : DragFeedback {

    override fun start(ctx: InteractionContext): Disposable {
        val interaction = MouseDragInteraction(ctx)

        interaction.loop(
            onStarted = {
            },
            onDragged = {
                val (target, _, _, dragDelta) = it

                val viewportPlotRect = target.geomPlotRect.subtract(dragDelta)
                target.setViewport(viewportPlotRect)
            },
            onCompleted = {
                println("PanGeomFeedback complete.")
                val (target, dragFrom, dragTo, dragDelta) = it

                val viewport = target.geomPlotRect.subtract(dragDelta)
                target.setViewport(viewport)

                onCompleted(dragTo.subtract(dragFrom), target)
                it.reset()
            },
            onAborted = {
                println("PanGeomFeedback abort.")
                it.reset()
                // ToDo: ...
            }
        )

        return object : Disposable {
            override fun dispose() {
                println("PanGeomFeedback dispose.")
                interaction.dispose()
            }
        }
    }
}