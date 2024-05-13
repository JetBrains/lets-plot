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
                it.target.pan(it.dragDelta)
            },
            onDragged = {
                it.target.pan(it.dragDelta)
            },
            onCompleted = {
                println("PanGeomFeedback complete.")
                val target = it.target
                target.pan(it.dragDelta)

                it.reset()
                onCompleted(DoubleVector.ZERO, target)
            },
            onAborted = {
                println("PanGeomFeedback abort.")
                it.reset()
                it.target.pan(it.dragDelta)
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