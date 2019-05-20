package jetbrains.datalore.visualization.plotDemo.model

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.visualization.base.svg.SvgColors
import jetbrains.datalore.visualization.base.svg.SvgCssResource
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.plot.base.render.GeomContext
import jetbrains.datalore.visualization.plot.base.render.svg.GroupComponent
import jetbrains.datalore.visualization.plot.gog.plot.Plot
import jetbrains.datalore.visualization.plot.gog.plot.PlotContainer
import jetbrains.datalore.visualization.plot.gog.plot.presentation.Style
import jetbrains.datalore.visualization.plot.gog.plot.theme.DefaultTheme
import jetbrains.datalore.visualization.plot.gog.plot.theme.Theme

abstract class SimpleDemoBase(protected val demoInnerSize: DoubleVector = DEFAULT_INNER_SIZE) {

    protected val demoComponentSize: DoubleVector
        get() = toComponentSize(demoInnerSize)
    protected open val padding: DoubleVector
        get() = DEFAULT_PADDING
    protected val theme: Theme = DEFAULT_THEME
    protected open val cssStyle: String = Style.css

    private fun toComponentSize(innerSize: DoubleVector): DoubleVector {
        return innerSize.add(padding.mul(2.0))
    }

    protected fun createSvgRootsFromPlots(plots: List<Plot>): List<SvgSvgElement> {
        val plotContainers = plots.map {
            PlotContainer(it, ValueProperty(demoInnerSize))
        }

        return plotContainers.map {
            it.ensureContentBuilt()
            it.svg
        }
    }

    protected fun createSvgRoots(demoGroups: List<GroupComponent>): List<SvgSvgElement> {
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
        private val DEFAULT_THEME = DefaultTheme()

        val EMPTY_GEOM_CONTEXT: GeomContext = EmptyGeomContext()
    }
}
