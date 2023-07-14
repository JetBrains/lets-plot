/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import kotlin.math.max
import kotlin.math.min

class DrawRectFeedback(
    private val onCompleted: ((Pair<DoubleRectangle, InteractionTarget>) -> Unit)
) : DragFeedback {
    private val rect = SvgRectElement().apply {
        strokeColor().set(Color.GRAY)
        fillColor().set(Color.TRANSPARENT)
        strokeWidth().set(2.0)
        x().set(0.0)
        y().set(0.0)
        width().set(0.0)
        height().set(0.0)
    }

    override fun start(ctx: InteractionContext): Disposable {

        val decorationsLayer = ctx.decorationsLayer
        val interaction = MouseDragInteraction(ctx)

        interaction.loop(
            onStarted = {
                println("DrawRectFeedback start.")
                updateRect(it.dragFrom, it.dragTo, it.target.geomBounds)
                decorationsLayer.children().add(rect)
            },
            onDragged = {
                println("DrawRectFeedback drag.")
                updateRect(it.dragFrom, it.dragTo, it.target.geomBounds)
            },
            onCompleted = {
                println("DrawRectFeedback complete.")
                decorationsLayer.children().remove(rect)
                val r = calcRect(it.dragFrom, it.dragTo, it.target.geomBounds)
                val target = it.target
                it.reset()
                onCompleted(r to target)
            },
            onAborted = {
                println("DrawRectFeedback abort.")
                decorationsLayer.children().remove(rect)
                it.reset()
            }
        )

        return object : Disposable {
            override fun dispose() {
                println("DrawRectFeedback dispose.")
                decorationsLayer.children().remove(rect)
                interaction.dispose()
            }
        }
    }

    private fun updateRect(
        dragFrom: DoubleVector,
        dragTo: DoubleVector,
        geomBounds: DoubleRectangle
    ) {
        calcRect(dragFrom, dragTo, geomBounds).let { r ->
            rect.x().set(r.left)
            rect.y().set(r.top)
            rect.width().set(r.width)
            rect.height().set(r.height)
        }
    }

    companion object {
        private fun calcRect(
            dragFrom: DoubleVector,
            dragTo: DoubleVector,
            geomBounds: DoubleRectangle
        ): DoubleRectangle {
            val left = min(dragFrom.x, dragTo.x)
            val top = min(dragFrom.y, dragTo.y)

            val r = DoubleRectangle(
                x = left,
                y = top,
                w = max(dragFrom.x, dragTo.x) - left,
                h = max(dragFrom.y, dragTo.y) - top
            )

            return geomBounds.intersect(r)!!
        }
    }
}