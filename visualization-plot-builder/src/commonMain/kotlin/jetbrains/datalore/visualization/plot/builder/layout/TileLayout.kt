package jetbrains.datalore.visualization.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector

interface TileLayout {
    fun doLayout(preferredSize: DoubleVector): TileLayoutInfo
}
