/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.tool

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.plot.builder.interact.ui.EventsManager
import jetbrains.datalore.vis.svg.SvgNode

class PanGeomFeedback(
    private val onCompleted: ((DoubleVector) -> Unit)
) : DragFeedback {

    override fun start(
        svgParent: SvgNode,
        eventsManager: EventsManager,
        geomBoundsList: List<DoubleRectangle>
    ): Disposable {
        val interaction = MouseDragInteraction(eventsManager, geomBoundsList)

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
                it.reset()
                onCompleted(v)
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