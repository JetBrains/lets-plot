/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.subPlots

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.render.svg.SvgUID
import jetbrains.datalore.plot.builder.FigureSvgRoot

class CompositeFigureSvgRoot(
    private val svgComponent: CompositeFigureSvgComponent,
    bounds: DoubleRectangle,
) : FigureSvgRoot(bounds) {

    val elements: List<FigureSvgRoot>
        get() = svgComponent.elements

    override fun buildFigureContent() {
        val id = SvgUID.get(PLOT_ID_PREFIX)

        // ToDo
//        svg.setStyle(object : SvgCssResource {
//            override fun css(): String {
//                return Style.generateCSS(plot.styleSheet, id, decorationLayerId = null)
//            }
//        })

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
