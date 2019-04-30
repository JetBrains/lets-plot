package jetbrains.datalore.visualization.plot.gog.plot.guide

import jetbrains.datalore.base.gcommon.base.Strings
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.core.render.svg.TextLabel
import jetbrains.datalore.visualization.plot.gog.plot.presentation.PlotLabelSpec

abstract class LegendBoxLayout protected constructor(private val myTitle: String, private val myLegendDirection: LegendDirection) {
    val titleHorizontalAnchor: TextLabel.HorizontalAnchor
    val titleVerticalAnchor: TextLabel.VerticalAnchor

    val isHorizontal: Boolean
        get() = myLegendDirection === LegendDirection.HORIZONTAL

    val titleBounds: DoubleRectangle
        get() {
            var origin = titleLocation
            val size = titleSize(myTitle)
            if (isHorizontal) {
                origin = DoubleVector(origin.x, origin.y - size.y / 2)
            }
            return DoubleRectangle(origin, size)
        }

    val graphOrigin: DoubleVector
        get() = if (isHorizontal) {
            DoubleVector(titleSize(myTitle).x, 0.0)
        } else DoubleVector(0.0, titleSize(myTitle).y)

    protected abstract val graphSize: DoubleVector

    val size: DoubleVector
        get() {
            val graphBounds = DoubleRectangle(graphOrigin, graphSize)
            val titleAndContent = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
                    .union(titleBounds)
                    .union(graphBounds)
            return titleAndContent.dimension
        }

    val titleLocation: DoubleVector
        get() {
            if (isHorizontal) {
                val graphSize = graphSize
                return DoubleVector(0.0, graphSize.y / 2)
            }
            return DoubleVector.ZERO
        }

    init {
        if (isHorizontal) {
            titleHorizontalAnchor = TextLabel.HorizontalAnchor.LEFT
            titleVerticalAnchor = TextLabel.VerticalAnchor.CENTER
        } else {
            titleHorizontalAnchor = TextLabel.HorizontalAnchor.LEFT
            titleVerticalAnchor = TextLabel.VerticalAnchor.TOP
        }
    }

    companion object {
        internal val TITLE_SPEC = PlotLabelSpec.LEGEND_TITLE
        internal val LABEL_SPEC = PlotLabelSpec.LEGEND_ITEM

        private fun titleSize(s: String): DoubleVector {
            return if (Strings.isNullOrEmpty(s)) {
                DoubleVector.ZERO
            } else TITLE_SPEC.dimensions(s.length)
        }
    }
}
