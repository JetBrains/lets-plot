package jetbrains.datalore.visualization.plot.builder.layout

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.builder.guide.Orientation
import jetbrains.datalore.visualization.plot.builder.layout.axis.GuideBreaks

class EmptyAxisLayout private constructor(xDomain: ClosedRange<Double>, yDomain: ClosedRange<Double>, private val myOrientation: Orientation) : AxisLayout {

    private val myAxisDomain: ClosedRange<Double>

    init {
        myAxisDomain = if (myOrientation.isHorizontal) xDomain else yDomain
    }

    override fun initialThickness(): Double {
        return 0.0
    }

    override fun doLayout(displaySize: DoubleVector, maxTickLabelsBoundsStretched: DoubleRectangle?): AxisLayoutInfo {
        val axisLength = if (myOrientation.isHorizontal) displaySize.x else displaySize.y
        val tickLabelsBounds = if (myOrientation.isHorizontal   // relative to axis component
        )
            DoubleRectangle(0.0, 0.0, axisLength, 0.0)
        else
            DoubleRectangle(0.0, 0.0, 0.0, axisLength)
        val breaks = GuideBreaks(emptyList<Any>(), emptyList(), emptyList())

        val builder = AxisLayoutInfo.Builder()
                .axisBreaks(breaks)
                .axisLength(axisLength)
                .orientation(myOrientation)
                .axisDomain(myAxisDomain)
                .tickLabelsBounds(tickLabelsBounds)

        return builder.build()
    }

    companion object {
        fun bottom(xDomain: ClosedRange<Double>, yDomain: ClosedRange<Double>): AxisLayout {
            return EmptyAxisLayout(xDomain, yDomain, Orientation.BOTTOM)
        }

        fun left(xDomain: ClosedRange<Double>, yDomain: ClosedRange<Double>): AxisLayout {
            return EmptyAxisLayout(xDomain, yDomain, Orientation.LEFT)
        }
    }
}
