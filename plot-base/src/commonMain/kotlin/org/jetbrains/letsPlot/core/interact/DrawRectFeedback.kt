/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

class DrawRectFeedback(
    private val onCompleted: ((DoubleRectangle) -> Unit)
) : DragFeedback {
    private val selectionRectSvg = SvgRectElement().apply {
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
                decorationsLayer.children().add(selectionRectSvg)
                decorationsLayer.children().add(viewportSvg)
            },
            onDragged = {
                println("DrawRectFeedback drag.")
                val (target, dragFrom, dragTo, _) = it

                val dragPlotRect = DoubleRectangle.span(dragFrom, dragTo)
                val selectionPlotRect = target.geomBounds.intersect(dragPlotRect) ?: return@loop

                drawRects(selectionRectSvg, selectionPlotRect)
                drawRects(viewportSvg, selectionPlotRect.shrinkToAspectRatio(target.geomBounds.dimension))
            },
            onCompleted = {
                println("DrawRectFeedback complete.")
                val (target, dragFrom, dragTo, _) = it
                decorationsLayer.children().remove(selectionRectSvg)
                decorationsLayer.children().remove(viewportSvg)

                val dragRect = DoubleRectangle.span(dragFrom, dragTo)
                val viewport = target.geomBounds.intersect(dragRect) ?: return@loop

                val dataBounds = target.applyViewport(viewport)

                onCompleted(dataBounds)
                it.reset()
            },
            onAborted = {
                println("DrawRectFeedback abort.")
                decorationsLayer.children().remove(selectionRectSvg)
                decorationsLayer.children().remove(viewportSvg)
                it.reset()
            }
        )

        return object : Disposable {
            override fun dispose() {
                println("DrawRectFeedback dispose.")
                decorationsLayer.children().remove(selectionRectSvg)
                decorationsLayer.children().remove(viewportSvg)
                interaction.dispose()
            }
        }
    }

}