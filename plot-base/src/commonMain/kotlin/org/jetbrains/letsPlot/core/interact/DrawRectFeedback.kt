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
    private val fixAspectRatio: Boolean,
    private val onCompleted: ((DoubleRectangle) -> Unit)
) : DragFeedback {

    private var selector: Selector = UnknownSelector()

    private abstract class Selector(
        private val limitToGeomBounds: Boolean
    ) {
        fun getSelection(
            dragFrom: DoubleVector,
            dragTo: DoubleVector,
            target: InteractionTarget
        ): DoubleRectangle {
            if (limitToGeomBounds) {
                val limitedDragTo = DoubleVector(
                    x = dragTo.x.coerceIn(target.geomBounds.left, target.geomBounds.right),
                    y = dragTo.y.coerceIn(target.geomBounds.top, target.geomBounds.bottom)
                )
                return onGetSelection(dragFrom, limitedDragTo, target)
            } else {
                return onGetSelection(dragFrom, dragTo, target)
            }
        }

        protected abstract fun onGetSelection(
            dragFrom: DoubleVector,
            dragTo: DoubleVector,
            target: InteractionTarget
        ): DoubleRectangle

        abstract fun isAcceptable(selection: DoubleRectangle): Boolean

        companion object {
            internal const val MIN_SIZE = 15.0
        }
    }

    private inner class UnknownSelector : Selector(limitToGeomBounds = true) {
        override fun onGetSelection(
            dragFrom: DoubleVector,
            dragTo: DoubleVector,
            target: InteractionTarget
        ): DoubleRectangle {
            val drag = dragTo.subtract(dragFrom)

            if (drag.length() > 20) {
                selector = when {
                    abs(drag.x) < 7 -> VerticalBandSelector()
                    abs(drag.y) < 7 -> HorizontalBandSelector()
                    fixAspectRatio -> CenterBoxSelector()
                    else -> CornerBoxSelector()
                }
            }

            return DoubleRectangle.span(dragFrom, dragTo)
        }

        override fun isAcceptable(selection: DoubleRectangle): Boolean {
            return false
        }
    }

    private inner class VerticalBandSelector : Selector(limitToGeomBounds = true) {
        override fun onGetSelection(
            dragFrom: DoubleVector,
            dragTo: DoubleVector,
            target: InteractionTarget
        ): DoubleRectangle {
            return DoubleRectangle.LTRB(
                left = target.geomBounds.left,
                right = target.geomBounds.right,
                top = minOf(dragFrom.y, dragTo.y),
                bottom = maxOf(dragFrom.y, dragTo.y)
            )
        }

        override fun isAcceptable(selection: DoubleRectangle): Boolean {
            return selection.height > MIN_SIZE
        }
    }

    private inner class HorizontalBandSelector : Selector(limitToGeomBounds = true) {
        override fun onGetSelection(
            dragFrom: DoubleVector,
            dragTo: DoubleVector,
            target: InteractionTarget
        ): DoubleRectangle {
            return DoubleRectangle.LTRB(
                left = minOf(dragFrom.x, dragTo.x),
                right = maxOf(dragFrom.x, dragTo.x),
                top = target.geomBounds.top,
                bottom = target.geomBounds.bottom
            )
        }

        override fun isAcceptable(selection: DoubleRectangle): Boolean {
            return selection.width > MIN_SIZE
        }
    }

    private inner class CenterBoxSelector : Selector(limitToGeomBounds = false) {
        override fun onGetSelection(
            dragFrom: DoubleVector,
            dragTo: DoubleVector,
            target: InteractionTarget
        ): DoubleRectangle {
            val drag = dragTo.subtract(dragFrom)
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
                origin = dragFrom.subtract(size),
                dimension = size.mul(2.0)
            )
        }

        override fun isAcceptable(selection: DoubleRectangle): Boolean {
            return selection.width > MIN_SIZE || selection.height > MIN_SIZE
        }
    }

    private inner class CornerBoxSelector : Selector(limitToGeomBounds = true) {
        override fun onGetSelection(
            dragFrom: DoubleVector,
            dragTo: DoubleVector,
            target: InteractionTarget
        ): DoubleRectangle {
            return DoubleRectangle.span(dragFrom, dragTo)
        }

        override fun isAcceptable(selection: DoubleRectangle): Boolean {
            return selection.width > MIN_SIZE || selection.height > MIN_SIZE
        }
    }

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
            onStarted = {
                val (target, dragFrom, dragTo, _) = it
                decorationsLayer.children().add(dragRectSvg)
                decorationsLayer.children().add(selectionSvg)

                val selection = selector.getSelection(dragFrom, dragTo, target)
                drawSelection(
                    geomBounds = target.geomBounds,
                    selection = selection,
                    isAcceptable = selector.isAcceptable(selection)
                )
            },
            onDragged = {
                val (target, dragFrom, dragTo, _) = it

                val selection = selector.getSelection(dragFrom, dragTo, target)
                drawSelection(
                    geomBounds = target.geomBounds,
                    selection = selection,
                    isAcceptable = selector.isAcceptable(selection)
                )
            },
            onCompleted = {
                val (target, dragFrom, dragTo, _) = it

                it.reset()

                val selection = selector.getSelection(dragFrom, dragTo, target)

                if (selector.isAcceptable(selection)) {
                    val dataBounds = target.applyViewport(selection)
                    onCompleted(dataBounds)
                }

                selector = UnknownSelector()
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
}