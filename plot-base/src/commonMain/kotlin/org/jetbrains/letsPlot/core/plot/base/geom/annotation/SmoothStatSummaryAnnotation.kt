/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.annotation

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.geom.GeomBase.Companion.overallAesBounds
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.AnnotationUtil.textColorAndLabelAlpha
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.PositionedAnnotation.Companion.DEFAULT_HORIZONTAL_PLACEMENT
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.PositionedAnnotation.Companion.DEFAULT_VERTICAL_PLACEMENT
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.PositionedAnnotation.HorizontalAnchor
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.PositionedAnnotation.HorizontalPlacement
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.PositionedAnnotation.VerticalAnchor
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.PositionedAnnotation.VerticalPlacement
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

object SmoothStatSummaryAnnotation {
    const val PADDING = 20

    fun isApplicable(ctx: GeomContext): Boolean {
        return ctx.annotation is PositionedAnnotation
    }

    fun build(
        root: SvgRoot,
        dataPoints: Iterable<DataPointAesthetics>,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val viewPort = coord.toClient(overallAesBounds(ctx)) ?: return
        val annotation = ctx.annotation as? PositionedAnnotation ?: return
        val textSizeGetter = AnnotationUtil.textSizeGetter(annotation.textStyle, ctx)
        val labels = ArrayList<AnnotationLabel>()

        for (dp in dataPoints) {

            val text = annotation.getAnnotationText(dp.index(), ctx.plotContext)
            val (textColor, _) = textColorAndLabelAlpha(
                annotation, dp.color(), dp.fill(),
                insideGeom = false
            )

            val label = AnnotationLabel(
                text = text,
                textSize = textSizeGetter(text, dp),
                textColor = textColor
            )

            labels.add(label)
        }

        val locations = getLocations(labels, annotation.horizontalPlacements, annotation.verticalPlacements, viewPort, coord)

        labels.forEachIndexed { index, label ->
            root.add(createAnnotationElement(label, locations[index], annotation.textStyle, ctx))
        }
    }

    private fun getLocations(
        labels: List<AnnotationLabel>,
        horizontalPlacements: List<HorizontalPlacement>,
        verticalPlacements: List<VerticalPlacement>,
        viewPort: DoubleRectangle,
        coord: CoordinateSystem
    ): List<DoubleVector> {
        val positionsCount = minOf(labels.size, maxOf(horizontalPlacements.size, verticalPlacements.size))

        val positionedLabels = ArrayList<AnnotationLabel>()
        val otherLabels = ArrayList<AnnotationLabel>()

        if (positionsCount > 1) {
            positionedLabels.addAll(labels.subList(0, positionsCount - 1))
            otherLabels.addAll(labels.subList(positionsCount - 1, labels.size))
        } else {
            otherLabels.addAll(labels)
        }

        val locations = ArrayList<DoubleVector>()

        positionedLabels.forEachIndexed { i, label ->
            locations.add(getLocation(
                listOf(label),
                horizontalPlacements.getOrNull(i) ?: horizontalPlacements.lastOrNull() ?: DEFAULT_HORIZONTAL_PLACEMENT,
                verticalPlacements.getOrNull(i) ?: verticalPlacements.lastOrNull() ?: DEFAULT_VERTICAL_PLACEMENT,
                viewPort,
                coord
            ))
        }

        val startLocation = getLocation(
            otherLabels,
            horizontalPlacements.getOrNull(positionsCount - 1) ?: horizontalPlacements.lastOrNull() ?: DEFAULT_HORIZONTAL_PLACEMENT,
            verticalPlacements.getOrNull(positionsCount - 1) ?: verticalPlacements.lastOrNull() ?: DEFAULT_VERTICAL_PLACEMENT,
            viewPort,
            coord
        )

        var verticalOffset = 0.0
        otherLabels.forEach { label ->
            locations.add(startLocation.add(DoubleVector(0.0, verticalOffset)))
            verticalOffset += label.textSize.y
        }

        return locations
    }

    private fun getLocation(
        labels: List<AnnotationLabel>,
        horizontalPlacement: HorizontalPlacement,
        verticalPlacement: VerticalPlacement,
        viewPort: DoubleRectangle,
        coord: CoordinateSystem
    ): DoubleVector {
        val blockSize = DoubleVector(
            labels.maxOf { it.textSize.x },
            labels.sumOf { it.textSize.y }
        )

        val x = horizontalPlacement.position?.let { coord.toClient(DoubleVector(it, 0))?.x }
            ?: when (horizontalPlacement.anchor) {
                HorizontalAnchor.LEFT -> viewPort.left + PADDING
                HorizontalAnchor.CENTER -> viewPort.center.x - blockSize.x / 2
                HorizontalAnchor.RIGHT -> viewPort.right - blockSize.x - PADDING
            }

        val y = verticalPlacement.position?.let { coord.toClient(DoubleVector(0, it))?.y }
            ?: when (verticalPlacement.anchor) {
                VerticalAnchor.TOP -> viewPort.top + PADDING
                VerticalAnchor.CENTER -> viewPort.center.y - blockSize.y / 2
                VerticalAnchor.BOTTOM -> viewPort.bottom - blockSize.y - PADDING
            }

        return DoubleVector(x, y)
    }

    private fun createAnnotationElement(
        label: AnnotationLabel,
        textLocation: DoubleVector,
        textStyle: TextStyle,
        ctx: GeomContext
    ): SvgGElement {

        val g = AnnotationUtil.createTextElement(
            label.text,
            textLocation,
            AnnotationUtil.TextParams(
                style = textStyle,
                color = label.textColor,
                hjust = "left",
            ),
            geomContext = ctx,
        )

        return g
    }

    private data class AnnotationLabel(
        val text: String,
        val textSize: DoubleVector,
        val textColor: Color
    )
}