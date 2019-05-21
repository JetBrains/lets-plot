package jetbrains.datalore.visualization.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector

interface PlotLayout {
    fun doLayout(preferredSize: DoubleVector): PlotLayoutInfo

    fun setPadding(top: Double, right: Double, bottom: Double, left: Double)
}
