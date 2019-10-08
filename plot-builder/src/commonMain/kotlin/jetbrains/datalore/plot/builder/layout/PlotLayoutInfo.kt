package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector

class PlotLayoutInfo(tiles: List<TileLayoutInfo>, val size: DoubleVector) {
    val tiles: List<TileLayoutInfo> = ArrayList(tiles)

}
