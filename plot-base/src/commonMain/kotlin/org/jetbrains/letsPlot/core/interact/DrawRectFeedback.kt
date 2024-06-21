/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGraphicsElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import kotlin.math.abs

class DrawRectFeedback(
    private val onCompleted: ((DoubleRectangle) -> Unit)
) : DragFeedback {
    private var fixAspectRatio: Boolean? = null

    private val dragRectSvg = SvgRectElement().apply {
        strokeColor().set(Color.BLACK)
        fillColor().set(Color.TRANSPARENT)
        strokeWidth().set(1.5)
        strokeDashArray().set("5,5")
        x().set(0.0)
        y().set(0.0)
        width().set(0.0)
        height().set(0.0)
    }

    private val selectionSvg = SvgPathElement().apply {
        fillColor().set(Color.LIGHT_GRAY)
        opacity().set(0.5)
        fillRule().set(SvgPathElement.FillRule.EVEN_ODD)
    }

    private fun drawSelection(geomBounds: DoubleRectangle, selection: DoubleRectangle) {
        dragRectSvg.apply {
            visibility().set(SvgGraphicsElement.Visibility.VISIBLE)
            x().set(selection.left)
            y().set(selection.top)
            width().set(selection.width)
            height().set(selection.height)
        }

        if (isSelectionAcceptable(selection)) {
            selectionSvg.apply {
                visibility().set(SvgGraphicsElement.Visibility.VISIBLE)
                d().set(SvgPathDataBuilder().apply {
                    moveTo(geomBounds.left, geomBounds.top)
                    lineTo(geomBounds.right, geomBounds.top)
                    lineTo(geomBounds.right, geomBounds.bottom)
                    lineTo(geomBounds.left, geomBounds.bottom)
                    closePath()
                    moveTo(selection.left, selection.top)
                    lineTo(selection.right, selection.top)
                    lineTo(selection.right, selection.bottom)
                    lineTo(selection.left, selection.bottom)
                    closePath()
                }.build())
            }
        } else {
            selectionSvg.visibility().set(SvgGraphicsElement.Visibility.HIDDEN)
        }
    }

    override fun start(ctx: InteractionContext): Disposable {
        val decorationsLayer = ctx.decorationsLayer
        val interaction = MouseDragInteraction(ctx)

        interaction.loop(
            onStarted = {
                val (target, dragFrom, dragTo, _) = it
                decorationsLayer.children().add(dragRectSvg)
                decorationsLayer.children().add(selectionSvg)

                val selection = getSelection(dragFrom, dragTo, target)
                drawSelection(
                    geomBounds = target.geomBounds,
                    selection = selection
                )
            },
            onDragged = {
                val (target, dragFrom, dragTo, _) = it

                val selection = getSelection(dragFrom, dragTo, target)
                drawSelection(
                    geomBounds = target.geomBounds,
                    selection = selection
                )
            },
            onCompleted = {
                val (target, dragFrom, dragTo, _) = it

                it.reset()

                val selection = getSelection(dragFrom, dragTo, target)

                if (isSelectionAcceptable(selection)) {
                    val dataBounds = target.applyViewport(selection)
                    onCompleted(dataBounds)
                }

                fixAspectRatio = null
                decorationsLayer.children().remove(dragRectSvg)
                decorationsLayer.children().remove(selectionSvg)
            },
            onAborted = {
                it.reset()
            }
        )

        return object : Disposable {
            override fun dispose() {
                decorationsLayer.children().remove(dragRectSvg)
                decorationsLayer.children().remove(selectionSvg)
                interaction.dispose()
            }
        }
    }

    private fun getSelection(
        dragFrom: DoubleVector,
        dragTo: DoubleVector,
        target: InteractionTarget
    ): DoubleRectangle {
        @Suppress("NAME_SHADOWING")
        val dragTo = DoubleVector(
            x = dragTo.x.coerceIn(target.geomBounds.left, target.geomBounds.right),
            y = dragTo.y.coerceIn(target.geomBounds.top, target.geomBounds.bottom)
        )
        val drag = dragTo.subtract(dragFrom)

        if (fixAspectRatio == null && drag.length() > 20) {
            fixAspectRatio = abs(drag.x) < 7
        }

        return if (fixAspectRatio == true) {
            val ratio = target.geomBounds.width / target.geomBounds.height
            val size = if (ratio > 1) {
                val dragHeight = abs(drag.y)

                val height = when {
                    dragFrom.y + dragHeight > target.geomBounds.bottom -> (target.geomBounds.bottom - dragFrom.y)
                    dragFrom.y - dragHeight < target.geomBounds.top -> (dragFrom.y - target.geomBounds.top)
                    else -> dragHeight
                }

                val width = (height * ratio)

                val finalWidth = when {
                    dragFrom.x + width > target.geomBounds.right -> target.geomBounds.right - dragFrom.x
                    dragFrom.x - width < target.geomBounds.left -> dragFrom.x - target.geomBounds.left
                    else -> width
                }

                val finalHeight = finalWidth / ratio

                DoubleVector(finalWidth, finalHeight)
            } else {
                // Simple implementation for now. Try to flip and use the code above.
                val width = abs(drag.x)
                val height = width / ratio
                DoubleVector(width, height)
            }

            DoubleRectangle(
                origin = dragFrom.subtract(size),
                dimension = size.mul(2.0)
            )
        } else {
            DoubleRectangle.span(dragFrom, dragTo)
                .intersect(target.geomBounds)
                ?: DoubleRectangle(dragFrom, DoubleVector.ZERO) // at least move the selection rect to the drag start point
        }
    }

    private fun isSelectionAcceptable(selection: DoubleRectangle): Boolean {
        return selection.width > 25 || selection.height > 25
    }
}