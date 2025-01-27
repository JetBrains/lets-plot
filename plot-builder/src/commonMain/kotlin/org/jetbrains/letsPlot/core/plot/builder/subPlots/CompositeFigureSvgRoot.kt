/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.subPlots

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgUID
import org.jetbrains.letsPlot.core.plot.builder.FigureSvgRoot
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgCssResource

class CompositeFigureSvgRoot(
    private val svgComponent: CompositeFigureSvgComponent,
    bounds: DoubleRectangle,
) : FigureSvgRoot(bounds) {

    val elements: List<FigureSvgRoot>
        get() = svgComponent.elements

    override fun buildFigureContent() {
        val id = SvgUID.get(PLOT_ID_PREFIX)

        svg.setStyle(object : SvgCssResource {
            override fun css(): String {
                return Style.generateCSS(svgComponent.styleSheet, id, decorationLayerId = null)
            }
        })

        svgComponent.rootGroup.id().set(id)
        svg.children().add(svgComponent.rootGroup)
    }

    override fun clearFigureContent() {
        svgComponent.clear()
    }

    private companion object {
        const val PLOT_ID_PREFIX = "p"
    }
}
