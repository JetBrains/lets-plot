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

    private val viewportSvg = SvgRectElement().apply {
        strokeColor().set(Color.BLACK)
        fillColor().set(Color.LIGHT_BLUE)
        strokeWidth().set(0.0)
        opacity().set(0.5)
        x().set(0.0)
        y().set(0.0)
        width().set(0.0)
        height().set(0.0)
    }

    private fun drawRects(r: SvgRectElement, rect: DoubleRectangle) {
        r.x().set(rect.left)
        r.y().set(rect.top)
        r.width().set(rect.width)
        r.height().set(rect.height)
    }

    override fun start(ctx: InteractionContext): Disposable {

        val decorationsLayer = ctx.decorationsLayer
        val interaction = MouseDragInteraction(ctx)

        interaction.loop(
            onStarted = {
                println("DrawRectFeedback start.")
                drawRects(it.dragFrom, it.dragTo, it.target.geomBounds)
                decorationsLayer.children().add(rect)
                decorationsLayer.children().add(viewportSvg)
            },
            onDragged = {
                println("DrawRectFeedback drag.")
                drawRects(it.dragFrom, it.dragTo, it.target.geomBounds)
            },
            onCompleted = {
                println("DrawRectFeedback complete.")
                decorationsLayer.children().remove(rect)
                decorationsLayer.children().remove(viewportSvg)

                val r = calcUserRect(it.dragFrom, it.dragTo, it.target.geomBounds)
                val target = it.target
                it.reset()
                onCompleted(r to target)
            },
            onAborted = {
                println("DrawRectFeedback abort.")
                decorationsLayer.children().remove(rect)
                decorationsLayer.children().remove(viewportSvg)
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

    private fun drawRects(
        dragFrom: DoubleVector,
        dragTo: DoubleVector,
        geomBounds: DoubleRectangle
    ) {
        drawRects(rect, calcUserRect(dragFrom, dragTo, geomBounds))
        drawRects(viewportSvg, calcViewportRect(dragFrom, dragTo, geomBounds))
    }

    companion object {
        private fun calcUserRect(
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

        private fun calcViewportRect(
            dragFrom: DoubleVector,
            dragTo: DoubleVector,
            geomBounds: DoubleRectangle
        ): DoubleRectangle {
            val userRect = calcUserRect(dragFrom, dragTo, geomBounds)
            return userRect.srinkToAspectRatio(geomBounds.dimension)
        }
    }
}