/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.tool

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Disposable

class PanGeomFeedback(
    private val onCompleted: ((Pair<DoubleVector, InteractionTarget>) -> Unit)
) : DragFeedback {

    override fun start(ctx: InteractionContext): Disposable {
        val interaction = MouseDragInteraction(ctx)

        interaction.loop(
            onStarted = {
                println("PanGeomFeedback start.")
            },
            onDragged = {
                println("PanGeomFeedback drag.")
            },
            onCompleted = {
                println("PanGeomFeedback complete.")
                val v = it.dragTo.subtract(it.dragFrom)
                val target = it.target
                it.reset()
                onCompleted(v to target)
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