/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.builder.PlotSvgComponent
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.plot.builder.defaultTheme.DefaultTheme
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.theme.Theme
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgColors
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgCssResource
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement

abstract class SimpleDemoBase(protected val demoInnerSize: DoubleVector = DEFAULT_INNER_SIZE) {

    val demoComponentSize: DoubleVector
        get() = toComponentSize(demoInnerSize)
    protected open val padding: DoubleVector
        get() = DEFAULT_PADDING
    protected val theme: Theme = DEFAULT_THEME
    protected open val cssStyle: String = Style.generateCSS(Style.default(), plotId = null, decorationLayerId = null)

    private fun toComponentSize(innerSize: DoubleVector): DoubleVector {
        return innerSize.add(padding.mul(2.0))
    }

    protected fun createSvgRootsFromPlots(plots: List<PlotSvgComponent>): List<SvgSvgElement> {
        val plotContainers = plots.map {
            PlotSvgRoot(it, null, DoubleVector.ZERO)
        }

        return plotContainers.map {
            it.ensureContentBuilt()
            it.svg
        }
    }

    fun createSvgRoots(demoGroups: List<GroupComponent>): List<SvgSvgElement> {
        return demoGroups.map {
            it.moveTo(padding)
            val svgRoot = createSvgRoot()
            svgRoot.children().add(it.rootGroup)
            svgRoot
        }
    }

    private fun createSvgRoot(): SvgSvgElement {
        val svg = SvgSvgElement()
        svg.width().set(demoComponentSize.x)
        svg.height().set(demoComponentSize.y)
        svg.addClass(Style.PLOT_CONTAINER)

        svg.setStyle(object : SvgCssResource {
            override fun css(): String = cssStyle
        })

        val viewport = DoubleRectangle(padding, demoInnerSize)
        val viewportRect = SvgRectElement(viewport)
        viewportRect.stroke().set(SvgColors.LIGHT_BLUE)
        viewportRect.fill().set(SvgColors.NONE)
        svg.children().add(viewportRect)

        return svg
    }


    companion object {
        private val DEFAULT_INNER_SIZE = DoubleVector(700.0, 350.0)
        private val DEFAULT_PADDING = DoubleVector(20.0, 20.0)
        private val DEFAULT_THEME = DefaultTheme.minimal2()

        val EMPTY_GEOM_CONTEXT: GeomContext = EmptyGeomContext()
    }
}
