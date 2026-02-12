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
import org.jetbrains.letsPlot.core.plot.base.geom.StatR2Geom.Companion.LabelX
import org.jetbrains.letsPlot.core.plot.base.geom.StatR2Geom.Companion.LabelY
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.AnnotationUtil.textColorAndLabelAlpha
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

object StatAnnotation {
    const val PADDING = 20

    fun build(
        root: SvgRoot,
        dataPoints: Iterable<DataPointAesthetics>,
        labelX: List<Pair<Double?, LabelX>>,
        labelY: List<Pair<Double?, LabelY>>,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val viewPort = coord.toClient(overallAesBounds(ctx)) ?: return
        val annotation = ctx.annotation ?: return
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

        val locations = getLocations(labels, labelX, labelY, viewPort, coord)

        labels.forEachIndexed { index, label ->
            root.add(createAnnotationElement(label, locations[index], annotation.textStyle, ctx))
        }
    }

    private fun getLocations(
        labels: List<AnnotationLabel>,
        labelX: List<Pair<Double?, LabelX>>,
        labelY: List<Pair<Double?, LabelY>>,
        viewPort: DoubleRectangle,
        coord: CoordinateSystem
    ): List<DoubleVector> {
        val positionsCount = minOf(labels.size, maxOf(labelX.size, labelY.size))

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
                labelX.getOrNull(i) ?: labelX.lastOrNull() ?: (null to LabelX.LEFT),
                labelY.getOrNull(i) ?: labelY.lastOrNull() ?: (null to LabelY.TOP),
                viewPort,
                coord
            ))
        }

        val startLocation = getLocation(
            otherLabels,
            labelX.getOrNull(positionsCount - 1) ?: labelX.lastOrNull() ?: (null to LabelX.LEFT),
            labelY.getOrNull(positionsCount - 1) ?: labelY.lastOrNull() ?: (null to LabelY.TOP),
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
        labelX: Pair<Double?, LabelX>,
        labelY: Pair<Double?, LabelY>,
        viewPort: DoubleRectangle,
        coord: CoordinateSystem
    ): DoubleVector {
        val blockSize = DoubleVector(
            labels.maxOf { it.textSize.x },
            labels.sumOf { it.textSize.y } // todo: paddings
        )

        val x = labelX.first?.let { coord.toClient(DoubleVector(it, 0))?.x }
            ?: when (labelX.second) {
                LabelX.LEFT -> viewPort.left + PADDING
                LabelX.CENTER -> viewPort.center.x - blockSize.x / 2
                LabelX.RIGHT -> viewPort.right - blockSize.x - PADDING
            }

        val y = labelY.first?.let { coord.toClient(DoubleVector(0, it))?.y }
            ?: when (labelY.second) {
                LabelY.TOP -> viewPort.top + PADDING
                LabelY.MIDDLE -> viewPort.center.y - blockSize.y / 2
                LabelY.BOTTOM -> viewPort.bottom - blockSize.y - PADDING
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