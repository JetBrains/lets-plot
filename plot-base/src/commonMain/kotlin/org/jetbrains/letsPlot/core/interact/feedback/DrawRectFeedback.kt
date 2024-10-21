/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.feedback

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.interact.InteractionContext
import org.jetbrains.letsPlot.core.interact.InteractionTarget
import org.jetbrains.letsPlot.core.interact.ToolFeedback
import org.jetbrains.letsPlot.core.interact.mouse.MouseDragInteraction
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGraphicsElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import kotlin.math.abs

class DrawRectFeedback(
    private val centerStart: Boolean,
    private val onCompleted: ((targetId: String?, dataBounds: DoubleRectangle, flipped: Boolean, SelectionMode) -> Unit)
) : ToolFeedback {

    private var selector: Selector = UnknownSelector()

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

    private fun drawSelection(geomBounds: DoubleRectangle, selection: DoubleRectangle, isAcceptable: Boolean) {
        dragRectSvg.apply {
            visibility().set(SvgGraphicsElement.Visibility.VISIBLE)
            x().set(selection.left)
            y().set(selection.top)
            width().set(selection.width)
            height().set(selection.height)
        }

        if (isAcceptable) {
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
            onStarted = { (target, dragFrom, dragTo, _) ->
                decorationsLayer.children().add(dragRectSvg)
                decorationsLayer.children().add(selectionSvg)

                val selection = selector.getSelection(dragFrom, dragTo, target)
                drawSelection(
                    geomBounds = target.geomBounds,
                    selection = selection,
                    isAcceptable = selector.isAcceptable(selection)
                )
            },
            onDragged = { (target, dragFrom, dragTo, _) ->
                val selection = selector.getSelection(dragFrom, dragTo, target)
                drawSelection(
                    geomBounds = target.geomBounds,
                    selection = selection,
                    isAcceptable = selector.isAcceptable(selection)
                )
            },
            onCompleted = {
                decorationsLayer.children().remove(dragRectSvg)
                decorationsLayer.children().remove(selectionSvg)

                val (target, dragFrom, dragTo, _) = it

                it.reset()

                val selection = selector.getSelection(dragFrom, dragTo, target)

                if (selector.isAcceptable(selection)) {
                    val (dataBounds, flipped) = target.applyViewport(selection, ctx)
                    onCompleted(target.id, dataBounds, flipped, selector.mode)
                }

                selector = UnknownSelector()
            },
            onAborted = MouseDragInteraction::reset
        )

        return object : Disposable {
            override fun dispose() {
                decorationsLayer.children().remove(dragRectSvg)
                decorationsLayer.children().remove(selectionSvg)
                interaction.dispose()
            }
        }
    }

    private abstract class Selector {
        abstract val mode: SelectionMode

        fun limitToGeomBounds(to: DoubleVector, target: InteractionTarget): DoubleVector {
            return DoubleVector(
                x = to.x.coerceIn(target.geomBounds.left, target.geomBounds.right),
                y = to.y.coerceIn(target.geomBounds.top, target.geomBounds.bottom)
            )
        }

        abstract fun getSelection(from: DoubleVector, to: DoubleVector, target: InteractionTarget): DoubleRectangle

        abstract fun isAcceptable(selection: DoubleRectangle): Boolean

        companion object {
            internal const val MIN_SIZE = 15.0
        }
    }

    private inner class UnknownSelector : Selector() {
        override val mode: SelectionMode = SelectionMode.BOX

        override fun getSelection(from: DoubleVector, to: DoubleVector, target: InteractionTarget): DoubleRectangle {
            val drag = to.subtract(from)
            if (drag.length() > 20) {
                selector = when {
                    abs(drag.x) < 7 -> HorizontalBandSelector()
                    abs(drag.y) < 7 -> VerticalBandSelector()
                    else -> BoxSelector()
                }
            }

            return DoubleRectangle.span(from, to)
        }

        override fun isAcceptable(selection: DoubleRectangle): Boolean = false
    }

    private inner class HorizontalBandSelector : Selector() {
        override val mode: SelectionMode = SelectionMode.HORIZONTAL_BAND

        override fun getSelection(from: DoubleVector, to: DoubleVector, target: InteractionTarget): DoubleRectangle {
            if (centerStart) {
                val length = abs(to.subtract(from).y)
                return DoubleRectangle.LTRB(
                    left = target.geomBounds.left,
                    right = target.geomBounds.right,
                    top = from.y - length,
                    bottom = from.y + length
                )
            } else {
                @Suppress("NAME_SHADOWING")
                val to = limitToGeomBounds(to, target)
                return DoubleRectangle.hvRange(
                    hRange = target.geomBounds.xRange(),
                    vRange = DoubleSpan(from.y, to.y)
                )
            }
        }

        override fun isAcceptable(selection: DoubleRectangle): Boolean = selection.height > MIN_SIZE
    }

    private inner class VerticalBandSelector : Selector() {
        override val mode: SelectionMode = SelectionMode.VERTICAL_BAND

        override fun getSelection(from: DoubleVector, to: DoubleVector, target: InteractionTarget): DoubleRectangle {
            if (centerStart) {
                val length = abs(to.subtract(from).x)
                return DoubleRectangle.LTRB(
                    left = from.x - length,
                    right = from.x + length,
                    top = target.geomBounds.top,
                    bottom = target.geomBounds.bottom
                )
            } else {
                @Suppress("NAME_SHADOWING")
                val to = limitToGeomBounds(to, target)
                return DoubleRectangle.hvRange(
                    hRange = DoubleSpan(from.x, to.x),
                    vRange = target.geomBounds.yRange()
                )
            }
        }

        override fun isAcceptable(selection: DoubleRectangle): Boolean = selection.width > MIN_SIZE
    }

    private inner class BoxSelector : Selector() {
        override val mode: SelectionMode = SelectionMode.BOX

        override fun getSelection(from: DoubleVector, to: DoubleVector, target: InteractionTarget): DoubleRectangle {
            if (centerStart) {
                val drag = to.subtract(from)
                val ratio = target.geomBounds.width / target.geomBounds.height
                val size = if (ratio > 1) {
                    val width = (abs(drag.y) * ratio)
                    val height = width / ratio
                    DoubleVector(width, height)
                } else {
                    val width = abs(drag.x)
                    val height = width / ratio
                    DoubleVector(width, height)
                }

                return DoubleRectangle(
                    origin = from.subtract(size),
                    dimension = size.mul(2.0)
                )
            } else {
                @Suppress("NAME_SHADOWING")
                val to = limitToGeomBounds(to, target)
                return DoubleRectangle.span(from, to)
            }
        }

        override fun isAcceptable(selection: DoubleRectangle): Boolean {
            return selection.width > MIN_SIZE || selection.height > MIN_SIZE
        }
    }

    enum class SelectionMode {
        HORIZONTAL_BAND,
        VERTICAL_BAND,
        BOX
    }

}
