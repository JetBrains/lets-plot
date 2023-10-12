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
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgColors
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement

class GridComponent(
    private val majorGrid: List<List<DoubleVector>>,
    private val minorGrid: List<List<DoubleVector>>,
    private val gridTheme: PanelGridTheme
) : SvgComponent() {
    override fun buildComponent() {
        // TODO: With non-linear transforms it's easier to use SvgClipPathElement
        /*
        val gridLineMinPos = start + 6
        val gridLineMaxPos = end - 6
         */

        if (gridTheme.showMinor()) {
            for (lineString in minorGrid) {
                //if (lineString >= gridLineMinPos && lineString <= gridLineMaxPos) {
                val elem = buildGridLine(lineString, gridTheme.minorLineWidth(), gridTheme.minorLineColor())
                rootGroup.children().add(elem)
                //}
            }
        }

        // Major grid.
        if (gridTheme.showMajor()) {
            for (lineString in majorGrid) {
                //if (lineString >= gridLineMinPos && lineString <= gridLineMaxPos) {
                val elem = buildGridLine(lineString, gridTheme.majorLineWidth(), gridTheme.majorLineColor())
                rootGroup.children().add(elem)
                //}
            }
        }
    }

    private fun buildGridLine(lineString: List<DoubleVector>, width: Double, color: Color): SvgPathElement {
        if (lineString.size < 2) {
            return SvgPathElement()
        }

        val elem = SvgPathElement()
        elem.strokeColor().set(color)
        elem.strokeWidth().set(width)
        elem.fill().set(SvgColors.NONE)
        elem.d().set(SvgPathDataBuilder().lineString(lineString).build())
        return elem
    }
}
