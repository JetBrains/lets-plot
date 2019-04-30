package jetbrains.datalore.visualization.plot.gog.plot.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.collections.Collections.unmodifiableList

class PlotLayoutInfo(tiles: List<TileLayoutInfo>, val size: DoubleVector) {
    val tiles: List<TileLayoutInfo>

    init {
        this.tiles = unmodifiableList(tiles)
    }
}
