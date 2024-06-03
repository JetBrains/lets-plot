/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.registration.Disposable

class PanGeomFeedback(
    private val onCompleted:((DoubleRectangle) -> Unit) = { _ -> println("PanGeomFeedback complete.") },
) : DragFeedback {

    override fun start(ctx: InteractionContext): Disposable {
        val interaction = MouseDragInteraction(ctx)

        interaction.loop(
            onStarted = {
            },
            onDragged = {
                val (target, _, _, dragDelta) = it

                val viewport = InteractionUtil.viewportFromTransform(target.geomBounds, translate = dragDelta)
                target.applyViewport(viewport)
            },
            onCompleted = {
                println("PanGeomFeedback complete.")
                val (target, _, _, dragDelta) = it

                val viewport = InteractionUtil.viewportFromTransform(target.geomBounds, translate = dragDelta)
                val dataBounds = target.applyViewport(viewport)

                onCompleted(dataBounds)
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