/*
 * Copyright (c) 2026. JetBrains s.r.o.
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
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LineSpec
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

class PositionedAnnotation(
    lines: List<LineSpec>,
    textStyle: TextStyle,
    useCustomColor: Boolean,
    useLayerColor: Boolean,
    val horizontalPlacements: List<HorizontalPlacement>,
    val verticalPlacements: List<VerticalPlacement>
): Annotation(lines, textStyle, useCustomColor, useLayerColor) {

    data class HorizontalPlacement(
        val position: Double?,
        val anchor: HorizontalAnchor
    )

    data class VerticalPlacement(
        val position: Double?,
        val anchor: VerticalAnchor
    )

    enum class HorizontalAnchor {
        LEFT, CENTER, RIGHT
    }

    enum class VerticalAnchor {
        TOP, CENTER, BOTTOM
    }

    companion object {
        val DEFAULT_HORIZONTAL_PLACEMENT = HorizontalPlacement(null, HorizontalAnchor.LEFT)
        val DEFAULT_VERTICAL_PLACEMENT = VerticalPlacement(null, VerticalAnchor.TOP)

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
        ): List<AnnotationLocation> {
            val positionsCount = minOf(labels.size, maxOf(horizontalPlacements.size, verticalPlacements.size))

            val positionedLabels = ArrayList<AnnotationLabel>()
            val otherLabels = ArrayList<AnnotationLabel>()

            if (positionsCount > 1) {
                positionedLabels.addAll(labels.subList(0, positionsCount - 1))
                otherLabels.addAll(labels.subList(positionsCount - 1, labels.size))
            } else {
                otherLabels.addAll(labels)
            }

            val locations = ArrayList<AnnotationLocation>()

            positionedLabels.forEachIndexed { i, label ->
                locations.add(getLocation(
                    horizontalPlacements.getOrNull(i) ?: horizontalPlacements.lastOrNull() ?: DEFAULT_HORIZONTAL_PLACEMENT,
                    verticalPlacements.getOrNull(i) ?: verticalPlacements.lastOrNull() ?: DEFAULT_VERTICAL_PLACEMENT,
                    viewPort,
                    coord
                ))
            }

            val startLocation = getLocation(
                horizontalPlacements.getOrNull(positionsCount - 1) ?: horizontalPlacements.lastOrNull() ?: DEFAULT_HORIZONTAL_PLACEMENT,
                verticalPlacements.getOrNull(positionsCount - 1) ?: verticalPlacements.lastOrNull() ?: DEFAULT_VERTICAL_PLACEMENT,
                viewPort,
                coord
            )

            var verticalOffset = 0.0
            otherLabels.forEach { label ->
                val loc = AnnotationLocation(
                    startLocation.position.add(DoubleVector(0.0, verticalOffset)),
                    startLocation.hAnchor,
                    startLocation.vAnchor
                )

                locations.add(loc)
                verticalOffset += label.textSize.y
            }

            return locations
        }

        private fun getLocation(
            horizontalPlacement: HorizontalPlacement,
            verticalPlacement: VerticalPlacement,
            viewPort: DoubleRectangle,
            coord: CoordinateSystem
        ): AnnotationLocation {

            val horizontal = horizontalPlacement.position?.let { coord.toClient(DoubleVector(it, 0))?.x }
                ?.let { it to Text.HorizontalAnchor.LEFT }
                ?: when (horizontalPlacement.anchor) {
                    HorizontalAnchor.LEFT -> viewPort.left + PADDING to Text.HorizontalAnchor.LEFT
                    HorizontalAnchor.CENTER -> viewPort.center.x to Text.HorizontalAnchor.MIDDLE
                    HorizontalAnchor.RIGHT -> viewPort.right - PADDING to Text.HorizontalAnchor.RIGHT
                }

            val vertical = verticalPlacement.position?.let { coord.toClient(DoubleVector(0, it))?.y }
                ?.let { it to Text.VerticalAnchor.TOP }
                ?: when (verticalPlacement.anchor) {
                    VerticalAnchor.TOP -> viewPort.top + PADDING to Text.VerticalAnchor.TOP
                    VerticalAnchor.CENTER -> viewPort.center.y to Text.VerticalAnchor.CENTER
                    VerticalAnchor.BOTTOM -> viewPort.bottom - PADDING to Text.VerticalAnchor.BOTTOM
                }
            return AnnotationLocation(
                DoubleVector(horizontal.first, vertical.first),
                horizontal.second,
                vertical.second
            )

        }

        private fun createAnnotationElement(
            label: AnnotationLabel,
            textLocation: AnnotationLocation,
            textStyle: TextStyle,
            ctx: GeomContext
        ): SvgGElement {

            val g = AnnotationUtil.createTextElement(
                label.text,
                textLocation.position,
                AnnotationUtil.TextParams(
                    style = textStyle,
                    color = label.textColor,
                    vjust = textLocation.vAnchor.name.lowercase(),
                    hjust = textLocation.hAnchor.name.lowercase(),
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

        private data class AnnotationLocation(
            val position: DoubleVector,
            val hAnchor: Text.HorizontalAnchor,
            val vAnchor: Text.VerticalAnchor
        )
    }
}