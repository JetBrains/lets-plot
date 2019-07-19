package jetbrains.datalore.visualization.plot.builder.guide

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.plot.base.render.svg.SvgComponent
import jetbrains.datalore.visualization.plot.base.render.svg.TextLabel
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Legend.OUTLINE_COLOR
import jetbrains.datalore.visualization.plot.builder.presentation.Style

abstract class LegendBox internal constructor(
    protected open val spec: LegendBoxSpec
) : SvgComponent() {

    var debug: Boolean = false

    private val title: String
        get() = spec.title

    val size: DoubleVector
        get() = spec.size

    internal fun hasTitle(): Boolean {
        return spec.hasTitle()
    }

    override fun buildComponent() {
        addClassName(Style.LEGEND)

        val outerBounds = DoubleRectangle(DoubleVector.ZERO, spec.size)
        addRectangle(spec.innerBounds, spec.theme.backgroundFill())
        addBorder(spec.innerBounds, OUTLINE_COLOR, 1.0)

        val innerGroup = SvgGElement()
        innerGroup.transform().set(buildTransform(spec.contentOrigin, 0.0))

        val l = spec.layout
        if (hasTitle()) {
            val label = createTitleLabel(
                l.titleLocation,
                l.titleHorizontalAnchor,
                l.titleVerticalAnchor
            )
            innerGroup.children().add(label.rootGroup)
        }

        val graphGroup = SvgGElement()
        graphGroup.transform().set(buildTransform(l.graphOrigin, 0.0))
        appendGuideContent(graphGroup)
        innerGroup.children().add(graphGroup)

        if (debug) {
            run {
                // outer bounds
                addBorder(outerBounds, Color.CYAN, 1.0)
            }
            run {
                // inner bounds
                val rect = SvgRectElement(spec.innerBounds)
                rect.fillColor().set(Color.BLACK)
                rect.strokeWidth().set(0.0)
                rect.fillOpacity().set(0.1)
                add(rect)
            }
            run {
                // content bounds
                addBorder(spec.contentBounds, Color.DARK_MAGENTA, 1.0)
            }
            run {
                // title bounds
                addBorder(l.titleBounds.add(spec.contentOrigin), Color.MAGENTA, 1.0)
            }
        }

        add(innerGroup)
    }

    protected fun addBorder(bounds: DoubleRectangle, strokeColor: Color, strokeWidth: Double) {
        add(createBorder(bounds, strokeColor, strokeWidth))
    }

    protected fun addRectangle(bounds: DoubleRectangle, fillColor: Color) {
        add(createRectangle(bounds, fillColor))
    }

    protected abstract fun appendGuideContent(contentRoot: SvgNode): DoubleVector

    private fun createTitleLabel(
        origin: DoubleVector,
        horizontalAnchor: TextLabel.HorizontalAnchor,
        verticalAnchor: TextLabel.VerticalAnchor
    ): TextLabel {
        val label = TextLabel(title)
        label.addClassName(Style.LEGEND_TITLE)
        label.setHorizontalAnchor(horizontalAnchor)
        label.setVerticalAnchor(verticalAnchor)
        label.moveTo(origin)
        return label
    }

    companion object {
        fun createBorder(bounds: DoubleRectangle, strokeColor: Color, strokeWidth: Double): SvgRectElement {
            // ToDo: to util
            val rect = SvgRectElement(bounds)
            rect.strokeColor().set(strokeColor)
            rect.strokeWidth().set(strokeWidth)
            rect.fillOpacity().set(0.0)
            return rect
        }

        protected fun createRectangle(bounds: DoubleRectangle, fillColor: Color): SvgRectElement {
            // ToDo: to util
            val rect = SvgRectElement(bounds)
            rect.fillColor().set(fillColor)
            return rect
        }
    }
}
