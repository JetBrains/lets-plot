package jetbrains.datalore.visualization.plotDemo.model

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgColors
import jetbrains.datalore.visualization.base.svg.SvgCssResource
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.plot.gog.core.render.GeomContext
import jetbrains.datalore.visualization.plot.gog.core.render.svg.GroupComponent
import jetbrains.datalore.visualization.plot.gog.plot.presentation.Style
import jetbrains.datalore.visualization.plot.gog.plot.theme.DefaultTheme
import jetbrains.datalore.visualization.plot.gog.plot.theme.Theme

abstract class SimpleDemoBase(protected val demoInnerSize: DoubleVector = DEFAULT_INNER_SIZE) {

    protected val demoComponentSize: DoubleVector
        get() = toComponentSize(demoInnerSize)
    protected val theme: Theme = DEFAULT_THEME
    protected open val cssStyle: String = Style.css

    protected fun createSvgRoots(demoGroups: List<GroupComponent>): List<SvgSvgElement> {
        return demoGroups.map {
            it.moveTo(PADDING)
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

        val viewport = DoubleRectangle(PADDING, demoInnerSize)
        val viewportRect = SvgRectElement(viewport)
        viewportRect.stroke().set(SvgColors.LIGHT_BLUE)
        viewportRect.fill().set(SvgColors.NONE)
        svg.children().add(viewportRect)

        return svg
    }


    companion object {
        private val DEFAULT_INNER_SIZE = DoubleVector(800.0, 300.0)
        private val PADDING = DoubleVector(20.0, 20.0)
        private val DEFAULT_THEME = DefaultTheme()

        protected fun toComponentSize(innerSize: DoubleVector): DoubleVector {
            return innerSize.add(PADDING.mul(2.0))
        }

        val EMPTY_GEOM_CONTEXT: GeomContext = EmptyGeomContext()
    }
}
