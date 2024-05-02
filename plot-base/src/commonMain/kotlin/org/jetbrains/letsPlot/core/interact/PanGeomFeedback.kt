/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Disposable

class PanGeomFeedback(
    private val onStarted: ((DoubleVector, InteractionTarget) -> Unit) = { _, _ -> println("PanGeomFeedback start.") },
    private val onCompleted:((DoubleVector, InteractionTarget) -> Unit) = { _, _ -> println("PanGeomFeedback complete.") },
    private val onDragged: ((DoubleVector, InteractionTarget) -> Unit) = { _, _ -> println("PanGeomFeedback drag.")}
) : DragFeedback {

    override fun start(ctx: InteractionContext): Disposable {
        val interaction = MouseDragInteraction(ctx)

        interaction.loop(
            onStarted = {
                println("PanGeomFeedback start.")
                val v = it.dragTo.subtract(it.dragFrom)
                val target = it.target
                target.pan(v)
                onStarted(v, it.target)
            },
            onDragged = {
                println("PanGeomFeedback dragged.")
                val v = it.dragTo.subtract(it.dragFrom)
                val target = it.target
                target.pan(v)
                onDragged(v, target)
            },
            onCompleted = {
                println("PanGeomFeedback complete.")
                val v = it.dragTo.subtract(it.dragFrom)
                val target = it.target
                target.pan(DoubleVector.ZERO)
                it.reset()
                onCompleted(v, target)
            },
            onAborted = {
                println("PanGeomFeedback abort.")
                it.reset()
                val target = it.target
                target.pan(DoubleVector.ZERO)
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