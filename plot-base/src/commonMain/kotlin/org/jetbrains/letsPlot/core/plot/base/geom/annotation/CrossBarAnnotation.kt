/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.annotation

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.GeomBase
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.BarAnnotation.contains
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel

object CrossBarAnnotation {
    fun build(
        root: SvgRoot,
        rectangles: Map<DataPointAesthetics, DoubleRectangle>,
        midLines: Map<Int, DoubleSegment>,
        fatten: Double,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val annotation = ctx.annotation ?: return
        val viewPort = GeomBase.overallAesBounds(ctx).let(coord::toClient) ?: return

        val padding = annotation.textStyle.size / 2
        val isHorizontallyOriented = ctx.flipped

        rectangles
            .map { (aes, rect) -> Triple(aes, rect, midLines[aes.index()]) }
            .forEach { (aes, rect, midLine) ->
                val text = annotation.getAnnotationText(aes.index(), ctx.plotContext)
                val textSize = AnnotationUtil
                    .textSizeGetter(annotation.textStyle, ctx)
                    .invoke(text, aes)
                    .add(DoubleVector(2 * padding, 2 * padding))
                val midLineStrokeWidth = AesScaling.strokeWidth(aes) * fatten

                var location = findTextLocation(
                    viewPort,
                    rect,
                    midLine,
                    midLineStrokeWidth,
                    textSize,
                    isHorizontallyOriented
                )

                val textRect = DoubleRectangle(
                    location.subtract(textSize.mul(0.5)),
                    textSize
                )

                val alpha: Double
                val labelColor = when {
                    rect.contains(textRect) -> {
                        alpha = 0.0
                        annotation.getTextColor(aes.fill())
                    }
                    else -> {
                        alpha = 0.75
                        annotation.getTextColor()
                    }
                }

                // separate label for each line
                val labels = MultilineLabel.splitLines(text)

                // Adjust location to center the first line vertically
                location = location.subtract(DoubleVector(0.0, annotation.textStyle.size * (labels.size - 1) / 2))

                labels.map { line ->
                    AnnotationUtil.createLabelElement(
                        line,
                        location,
                        textParams = AnnotationUtil.TextParams(
                            style = annotation.textStyle,
                            color = labelColor,
                            hjust = "middle",
                            vjust = "top",
                            fill = ctx.backgroundColor,
                            alpha = alpha
                        ),
                        geomContext = ctx,
                    ).also {
                        location = location.add(DoubleVector(0.0, annotation.textStyle.size))
                    }
                }.forEach(root::add)
            }
    }

    private fun findTextLocation(viewPort: DoubleRectangle, rect: DoubleRectangle, midLine: DoubleSegment?, strokeWidth: Double, textSize: DoubleVector, isHorizontallyOriented: Boolean): DoubleVector {
        return if (isHorizontallyOriented) {
            findHorizontalLocation(viewPort, rect, midLine, strokeWidth, textSize)
        } else {
            findVerticalLocation(viewPort, rect, midLine, strokeWidth, textSize)
        }
    }

    private fun findHorizontalLocation(viewPort: DoubleRectangle, rect: DoubleRectangle, midLine: DoubleSegment?, strokeWidth: Double, textSize: DoubleVector): DoubleVector {
        val leftOuterRect = DoubleRectangle(
            DoubleVector(viewPort.origin.x, rect.origin.y),
            DoubleVector(rect.left - viewPort.origin.x, rect.height)
        )
        val rightOuterRect = DoubleRectangle(
            DoubleVector(rect.right, rect.top),
            DoubleVector(viewPort.right - rect.right, rect.height)
        )

        if (midLine != null) {
            val leftInnerRect = DoubleRectangle(
                DoubleVector(midLine.start.x + strokeWidth / 2, rect.origin.y),
                DoubleVector(rect.right - midLine.start.x - strokeWidth / 2, rect.height)
            )
            val rightInnerRect = DoubleRectangle(
                rect.origin,
                DoubleVector(midLine.start.x - strokeWidth / 2 - rect.origin.x, rect.height)
            )

            return when {
                leftInnerRect.width > textSize.x -> leftInnerRect.center
                rightInnerRect.width > textSize.x -> rightInnerRect.center
                leftOuterRect.width > textSize.x -> DoubleVector(leftOuterRect.right - textSize.x / 2, rect.center.y)
                rightOuterRect.width > textSize.x -> DoubleVector(rightOuterRect.left + textSize.x / 2, rect.center.y)
                else -> rect.center
            }
        } else {
            return when {
                rect.width > textSize.x -> rect.center
                leftOuterRect.width > textSize.x -> DoubleVector(leftOuterRect.right - textSize.x / 2, rect.center.y)
                rightOuterRect.width > textSize.x -> DoubleVector(rightOuterRect.left + textSize.x / 2, rect.center.y)
                else -> rect.center
            }
        }
    }

    private fun findVerticalLocation(viewPort: DoubleRectangle, rect: DoubleRectangle, midLine: DoubleSegment?, strokeWidth: Double, textSize: DoubleVector): DoubleVector {
        val upperOuterRect = DoubleRectangle(
            DoubleVector(rect.origin.x, viewPort.origin.y),
            DoubleVector(rect.width, rect.top - viewPort.origin.y)
        )
        val lowerOuterRect = DoubleRectangle(
            DoubleVector(rect.origin.x, rect.bottom),
            DoubleVector(rect.width, viewPort.bottom - rect.bottom)
        )

        if (midLine != null) {
            val upperInnerRect = DoubleRectangle(
                rect.origin,
                DoubleVector(rect.width, midLine.start.y - strokeWidth / 2 - rect.origin.y)
            )
            val lowerInnerRect = DoubleRectangle(
                DoubleVector(rect.origin.x, midLine.start.y),
                DoubleVector(rect.width, rect.origin.y + rect.height - midLine.start.y + strokeWidth / 2)
            )

            return when {
                upperInnerRect.height > textSize.y -> DoubleVector(rect.center.x, upperInnerRect.bottom - textSize.y / 2)
                lowerInnerRect.height > textSize.y -> DoubleVector(rect.center.x, lowerInnerRect.top + textSize.y / 2)
                upperOuterRect.height > textSize.y -> DoubleVector(rect.center.x, upperOuterRect.bottom - textSize.y / 2)
                lowerOuterRect.height > textSize.y -> DoubleVector(rect.center.x, lowerOuterRect.top + textSize.y / 2)
                else -> rect.center
            }
        } else {
            return when {
                rect.height > textSize.y -> rect.center
                upperOuterRect.height > textSize.y -> DoubleVector(rect.center.x, upperOuterRect.bottom - textSize.y / 2)
                lowerOuterRect.height > textSize.y -> DoubleVector(rect.center.x, lowerOuterRect.top + textSize.y / 2)
                else -> rect.center
            }
        }
    }
}