/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.tool

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.interact.ui.EventsManager
import jetbrains.datalore.vis.svg.SvgNode
import jetbrains.datalore.vis.svg.SvgRectElement
import kotlin.math.max
import kotlin.math.min

class DrawRectFeedback(
    private val onCompleted: ((DoubleRectangle) -> Unit)
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

    override fun start(
        svgParent: SvgNode,
        eventsManager: EventsManager,
        geomBoundsList: List<DoubleRectangle>
    ): Disposable {
        val interaction = MouseDragInteraction(eventsManager, geomBoundsList)

        interaction.loop(
            onStarted = {
                println("DrawRectFeedback start.")
                calcRect(it.dragFrom, it.dragTo, it.geomBounds).let {
                    rect.x().set(it.left)
                    rect.y().set(it.top)
                    rect.width().set(it.width)
                    rect.height().set(it.height)
                }
                svgParent.children().add(rect)
            },
            onDragged = {
                println("DrawRectFeedback drag.")
                calcRect(it.dragFrom, it.dragTo, it.geomBounds).let {
                    rect.x().set(it.left)
                    rect.y().set(it.top)
                    rect.width().set(it.width)
                    rect.height().set(it.height)
                }
            },
            onCompleted = {
                println("DrawRectFeedback complete.")
                svgParent.children().remove(rect)
                val r = calcRect(it.dragFrom, it.dragTo, it.geomBounds)
                // translate to "geom" space.
                val translated = r.subtract(it.geomBounds.origin)
                it.reset()
                onCompleted(translated)
            },
            onAborted = {
                println("DrawRectFeedback abort.")
                svgParent.children().remove(rect)
                it.reset()
            }
        )

        return object : Disposable {
            override fun dispose() {
                println("DrawRectFeedback dispose.")
                svgParent.children().remove(rect)
                interaction.dispose()
            }
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