/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.svg.StrokeDashArraySupport
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.lineString
import org.jetbrains.letsPlot.core.plot.base.theme.PanelGridTheme
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayoutInfo
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils.transformTranslate

class GridComponent(
    private val majorBreaks: List<DoubleVector>,
    private val minorBreaks: List<DoubleVector>,
    private val majorGrid: List<List<DoubleVector>>,
    private val minorGrid: List<List<DoubleVector>>,
    axisInfo: AxisLayoutInfo,
    private val gridTheme: PanelGridTheme,
    private val panOffset: DoubleVector,
) : SvgComponent() {
    private val container = SvgGElement()

    private val length = axisInfo.axisLength
    private val orientation = axisInfo.orientation

    private val start = 0.0
    private val end: Double = length

    override fun buildComponent() {
        rootGroup.children().add(container)

        if (panOffset != DoubleVector.ZERO) {
            val delta = when (orientation.isHorizontal) {
                true -> DoubleVector(panOffset.x, 0)
                false -> DoubleVector(0, panOffset.y)
            }

            transformTranslate(container, delta)
        }

        if (gridTheme.showMinor()) {
            buildGrid(minorBreaks, minorGrid, gridTheme.minorLineWidth(), gridTheme.minorLineColor(), gridTheme.minorLineType())
        }

        if (gridTheme.showMajor()) {
            buildGrid(majorBreaks, majorGrid, gridTheme.majorLineWidth(), gridTheme.majorLineColor(), gridTheme.majorLineType())
        }
    }

    private fun buildGrid(breaks: List<DoubleVector>, grid: List<List<DoubleVector>>, lineWidth: Double, lineColor: Color, lineType: LineType) {
        breaks.forEachIndexed { index, br ->
            val loc = when (orientation.isHorizontal) {
                true -> br.x + panOffset.x
                false -> br.y + panOffset.y
            }

            if (loc in start..end) {
                val elem = buildGridLine(grid[index], lineWidth, lineColor, lineType)
                container.children().add(elem)
            }
        }
    }

    private fun buildGridLine(
        lineString: List<DoubleVector>,
        width: Double,
        color: Color,
        lineType: LineType
    ): SvgNode {
        val shapeElem: SvgShape = when {
            lineString.size == 2 -> SvgLineElement(lineString[0].x, lineString[0].y, lineString[1].x, lineString[1].y)
            lineString.size < 2 -> SvgPathElement()
            else -> SvgPathElement(SvgPathDataBuilder().lineString(lineString).build())
        }

        shapeElem.strokeColor().set(color)
        shapeElem.strokeWidth().set(width)
        StrokeDashArraySupport.apply(shapeElem, width, lineType)
        shapeElem.fill().set(SvgColors.NONE)
        return shapeElem as SvgNode
    }
}
