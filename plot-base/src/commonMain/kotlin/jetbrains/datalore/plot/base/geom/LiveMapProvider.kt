package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.visualization.base.canvasFigure.CanvasFigure

interface LiveMapProvider {
    fun createLiveMap(
        aesthetics: Aesthetics,
        dataAccess: MappedDataAccess,
        dimension: DoubleVector,
        layers: List<LiveMapLayerData>
    ): CanvasFigure
}
