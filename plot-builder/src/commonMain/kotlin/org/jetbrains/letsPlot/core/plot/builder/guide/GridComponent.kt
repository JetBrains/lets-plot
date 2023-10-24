/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.lineString
import org.jetbrains.letsPlot.core.plot.base.theme.PanelGridTheme
import org.jetbrains.letsPlot.datamodel.svg.dom.*

class GridComponent(
    private val majorGrid: List<List<DoubleVector>>,
    private val minorGrid: List<List<DoubleVector>>,
    private val gridTheme: PanelGridTheme
) : SvgComponent() {
    override fun buildComponent() {

        if (gridTheme.showMinor()) {
            for (lineString in minorGrid) {
                val elem = buildGridLine(lineString, gridTheme.minorLineWidth(), gridTheme.minorLineColor())
                rootGroup.children().add(elem)
            }
        }

        // Major grid.
        if (gridTheme.showMajor()) {
            for (lineString in majorGrid) {
                val elem = buildGridLine(lineString, gridTheme.majorLineWidth(), gridTheme.majorLineColor())
                rootGroup.children().add(elem)
            }
        }
    }

    private fun buildGridLine(lineString: List<DoubleVector>, width: Double, color: Color): SvgNode {
        val shapeElem: SvgShape = when {
            lineString.size == 2 -> SvgLineElement(lineString[0].x, lineString[0].y, lineString[1].x, lineString[1].y )
            lineString.size < 2 -> SvgPathElement()
            else -> SvgPathElement(SvgPathDataBuilder().lineString(lineString).build())
        }

        shapeElem.strokeColor().set(color)
        shapeElem.strokeWidth().set(width)
        shapeElem.fill().set(SvgColors.NONE)
        return shapeElem as SvgNode
    }
}
