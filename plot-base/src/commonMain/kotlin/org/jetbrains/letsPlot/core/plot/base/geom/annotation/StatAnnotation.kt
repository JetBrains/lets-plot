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
        labelX: LabelX,
        labelY: LabelY,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val viewPort = coord.toClient(overallAesBounds(ctx)) ?: return
        val annotation = ctx.annotation ?: return
        val textSizeGetter = AnnotationUtil.textSizeGetter(annotation.textStyle, ctx)
        val labels = ArrayList<AnnotationLabel>()

        for ((i, dp) in dataPoints.withIndex()) {

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

        val startLocation = getLocation(labels, labelX, labelY, viewPort)

        var verticalOffset = 0.0
        labels.forEach { label ->
            val location = startLocation.add(DoubleVector(0.0, verticalOffset))

            root.add(createAnnotationElement(label, location, annotation.textStyle, ctx))

            verticalOffset += label.textSize.y
        }
    }

    private fun getLocation(labels: List<AnnotationLabel>, labelX: LabelX, labelY: LabelY, viewPort: DoubleRectangle): DoubleVector {
        val blockSize = DoubleVector(
            labels.maxOf { it.textSize.x },
            labels.sumOf { it.textSize.y } // todo: paddings
        )

        val x = when (labelX) {
            LabelX.LEFT -> viewPort.left + PADDING
            LabelX.CENTER -> viewPort.center.x - blockSize.x / 2
            LabelX.RIGHT -> viewPort.right - blockSize.x

        }

        val y = when (labelY) {
            LabelY.TOP -> viewPort.top + PADDING
            LabelY.MIDDLE -> viewPort.center.y - blockSize.y / 2
            LabelY.BOTTOM -> viewPort.bottom - blockSize.y
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