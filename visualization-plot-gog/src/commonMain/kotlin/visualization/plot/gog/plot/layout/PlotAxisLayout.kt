package jetbrains.datalore.visualization.plot.gog.plot.layout

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.visualization.plot.gog.core.scale.Scale2
import jetbrains.datalore.visualization.plot.gog.plot.coord.CoordProvider
import jetbrains.datalore.visualization.plot.gog.plot.guide.Orientation
import jetbrains.datalore.visualization.plot.gog.plot.layout.axis.AxisBreaksUtil
import jetbrains.datalore.visualization.plot.gog.plot.layout.axis.AxisLayouter
import jetbrains.datalore.visualization.plot.gog.plot.presentation.PlotLabelSpec
import jetbrains.datalore.visualization.plot.gog.plot.theme.AxisTheme

class PlotAxisLayout private constructor(private val myScale: Scale2<Double>, private val myXDomain: ClosedRange<Double>, private val myYDomain: ClosedRange<Double>, private val myCoordProvider: CoordProvider,
                                         private val myTheme: AxisTheme, private val myOrientation: Orientation) : AxisLayout {

    override fun initialThickness(): Double {
        if (myTheme.showTickMarks() || myTheme.showTickLabels()) {
            val v = myTheme.tickLabelDistance()
            return if (myTheme.showTickLabels()) {
                v + initialTickLabelSize(myOrientation)
            } else v
        }
        return 0.0
    }

    override fun doLayout(displaySize: DoubleVector, maxTickLabelsBoundsStretched: DoubleRectangle?): AxisLayoutInfo {
        val layouter = createLayouter(displaySize)
        return layouter.doLayout(axisLength(displaySize, myOrientation), maxTickLabelsBoundsStretched)
    }

    private fun createLayouter(displaySize: DoubleVector): AxisLayouter {
        val domains = myCoordProvider.adjustDomains(myXDomain, myYDomain, displaySize)
        val axisDomain = axisDomain(domains, myOrientation)

        val breaksProvider = AxisBreaksUtil.createAxisBreaksProvider(myScale, axisDomain)
        return AxisLayouter.create(myOrientation, axisDomain, breaksProvider, myTheme)
    }

    companion object {
        private val TICK_LABEL_SPEC = PlotLabelSpec.AXIS_TICK

        fun bottom(scale: Scale2<Double>, xDomain: ClosedRange<Double>, yDomain: ClosedRange<Double>, coordProvider: CoordProvider, theme: AxisTheme): AxisLayout {
            return PlotAxisLayout(scale, xDomain, yDomain, coordProvider,
                    theme,
                    Orientation.BOTTOM)
        }

        fun left(scale: Scale2<Double>, xDomain: ClosedRange<Double>, yDomain: ClosedRange<Double>, coordProvider: CoordProvider, theme: AxisTheme): AxisLayout {
            return PlotAxisLayout(scale, xDomain, yDomain, coordProvider,
                    theme,
                    Orientation.LEFT)
        }

        private fun initialTickLabelSize(orientation: Orientation): Double {
            return if (orientation.isHorizontal)
                TICK_LABEL_SPEC.height()
            else
                TICK_LABEL_SPEC.width(1)
        }

        private fun axisLength(displaySize: DoubleVector, orientation: Orientation): Double {
            return if (orientation.isHorizontal)
                displaySize.x
            else
                displaySize.y
        }

        private fun axisDomain(xyDomains: Pair<ClosedRange<Double>, ClosedRange<Double>>, orientation: Orientation): ClosedRange<Double> {
            return if (orientation.isHorizontal)
                xyDomains.first!!
            else
                xyDomains.second!!
        }
    }
}
