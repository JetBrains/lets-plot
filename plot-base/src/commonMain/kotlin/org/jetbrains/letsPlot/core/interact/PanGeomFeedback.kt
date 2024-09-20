/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Disposable
import kotlin.math.abs

class PanGeomFeedback(
    private val onCompleted: ((DoubleRectangle) -> Unit) = { _ -> println("PanGeomFeedback complete.") },
) : DragFeedback {

    private var panningMode: PanningMode? = null

    override fun start(ctx: InteractionContext): Disposable {
        val interaction = MouseDragInteraction(ctx)

        interaction.loop(
            onStarted = {},
            onDragged = { (target, dragFrom, dragTo, dragDelta) ->
                when (panningMode) {
                    PanningMode.HORIZONTAL -> DoubleVector(dragDelta.x, 0.0)
                    PanningMode.VERTICAL -> DoubleVector(0.0, dragDelta.y)
                    PanningMode.FREE -> dragDelta
                    null -> {
                        val drag = dragTo.subtract(dragFrom)
                        if (drag.length() > 15) {
                            when {
                                abs(drag.x) < 7 -> {
                                    panningMode = PanningMode.VERTICAL
                                    DoubleVector(0.0, drag.y)
                                }

                                abs(drag.y) < 7 -> {
                                    panningMode = PanningMode.HORIZONTAL
                                    DoubleVector(drag.x, 0.0)
                                }

                                else -> {
                                    panningMode = PanningMode.FREE
                                    drag
                                }
                            }
                        } else {
                            null
                        }
                    }
                }?.let { delta ->
                    val viewport = InteractionUtil.viewportFromTransform(target.geomBounds, translate = delta)
                    target.applyViewport(viewport, ctx)
                }
            },
            onCompleted = {
                println("PanGeomFeedback complete.")
                val (target, _, _, dragDelta) = it

                val viewport = InteractionUtil.viewportFromTransform(target.geomBounds, translate = dragDelta)
                val dataBounds = target.applyViewport(viewport, ctx)

                it.reset()
                panningMode = null

                onCompleted(dataBounds)
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

    private enum class PanningMode {
        HORIZONTAL,
        VERTICAL,
        FREE
    }

}