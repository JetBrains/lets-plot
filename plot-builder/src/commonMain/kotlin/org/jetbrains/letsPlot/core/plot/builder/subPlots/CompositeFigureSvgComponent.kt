/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.subPlots

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.render.svg.StrokeDashArraySupport
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.FigureSvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

class CompositeFigureSvgComponent constructor(
    val elements: List<FigureSvgRoot>,
    val size: DoubleVector,
    val theme: Theme,
//    val styleSheet: StyleSheet,
) : SvgComponent() {

    override fun buildComponent() {
        // ToDo: add title, subtitle, caption
        val plotTheme = theme.plot()
        if (plotTheme.showBackground()) {
            val r = DoubleRectangle(DoubleVector.ZERO, size)
            val plotInset = Thickness.uniform(plotTheme.backgroundStrokeWidth() / 2)
            val backgroundRect = plotInset.shrinkRect(r)
            add(SvgRectElement(backgroundRect).apply {
                fillColor().set(plotTheme.backgroundFill())
                strokeColor().set(plotTheme.backgroundColor())
                strokeWidth().set(plotTheme.backgroundStrokeWidth())
                StrokeDashArraySupport.apply(this, plotTheme.backgroundStrokeWidth(), plotTheme.backgroundLineType())
            })
        }
    }

    override fun clear() {
        super.clear()
    }
}