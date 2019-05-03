package jetbrains.datalore.visualization.plot.gog.plot.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

interface AxisLayout {
    fun initialThickness(): Double

    fun doLayout(displaySize: DoubleVector, maxTickLabelsBoundsStretched: DoubleRectangle?): AxisLayoutInfo
}
