/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.svg.StrokeDashArraySupport
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.lineString
import org.jetbrains.letsPlot.core.plot.base.theme.PanelGridTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PanelTheme
import org.jetbrains.letsPlot.datamodel.svg.dom.*

class GridComponent constructor(
    private val majorGrid: List<List<DoubleVector>>,
    private val minorGrid: List<List<DoubleVector>>,
    private val isHorizontal: Boolean,
    private val isOrthogonal: Boolean,
    geomContentBounds: DoubleRectangle,
    private val gridTheme: PanelGridTheme,
    panelTheme: PanelTheme
) : SvgComponent() {
    private val container = SvgGElement()
    private val start = 0.0
    private val end: Double = if (isHorizontal) geomContentBounds.width else geomContentBounds.height
    private val gridArea = geomContentBounds
        .subtract(geomContentBounds.origin)
        .let { gridArea ->
            if (!panelTheme.showRect() && !panelTheme.showBorder()) {
                // BBC style - no border, no padding, show grid lines until the very edge
                return@let gridArea
            }

            // reduce grid area by 3px to avoid grid lines to be drawn on the edge (for aesthetic reasons)
            val noGridMargin = when (isHorizontal) {
                true -> Thickness(top = 3.0, bottom = 3.0)
                false -> Thickness(right = 3.0, left = 3.0)
            }

            return@let noGridMargin.shrinkRect(gridArea)
        }

    override fun buildComponent() {
        rootGroup.children().add(container)

        if (gridTheme.showMinor()) {
            buildGrid(
                minorGrid,
                gridTheme.minorLineWidth(),
                gridTheme.minorLineColor(),
                gridTheme.minorLineType()
            )
        }

        if (gridTheme.showMajor()) {
            buildGrid(
                majorGrid,
                gridTheme.majorLineWidth(),
                gridTheme.majorLineColor(),
                gridTheme.majorLineType()
            )
        }
    }

    private fun buildGrid(
        grid: List<List<DoubleVector>>,
        lineWidth: Double,
        lineColor: Color,
        lineType: LineType
    ) {
        val visibleGridLines =
            if (isOrthogonal) {
                grid
                    .map { (p) ->
                        when (isHorizontal) {
                            true -> listOf(DoubleVector(start, p.y), DoubleVector(end, p.y))
                            false -> listOf(DoubleVector(p.x, start), DoubleVector(p.x, end))
                        }
                    }
                    .filter { line -> line.any { p -> p in gridArea } }
            } else {
                // Non-orthogonal grid is always visible and don't support panning
                grid
            }

        val elems = visibleGridLines.map { buildGridLine(it, lineWidth, lineColor, lineType) }
        container.children().addAll(elems)
    }

    private fun buildGridLine(
        lineString: List<DoubleVector>,
        width: Double,
        color: Color,
        lineType: LineType
    ): SvgNode {
        val shapeElem: SvgShape = when {
            lineString.size == 2 -> SvgLineElement(lineString[0].x, lineString[0].y, lineString[1].x, lineString[1].y)
            lineString.size > 2 -> SvgPathElement(SvgPathDataBuilder().lineString(lineString).build())
            else -> SvgPathElement()
        }

        shapeElem.strokeColor().set(color)
        shapeElem.strokeWidth().set(width)
        StrokeDashArraySupport.apply(shapeElem, width, lineType)
        shapeElem.fill().set(SvgColors.NONE)
        return shapeElem as SvgNode
    }
}
