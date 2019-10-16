package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.canvasFigure.CanvasFigure

interface LiveMapProvider {
    fun createLiveMap(dimension: DoubleVector): CanvasFigure
}
