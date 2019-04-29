package jetbrains.datalore.visualization.plot.gog.plot.layout

import jetbrains.datalore.base.geometry.DoubleVector

interface TileLayout {
    fun doLayout(preferredSize: DoubleVector): TileLayoutInfo
}
