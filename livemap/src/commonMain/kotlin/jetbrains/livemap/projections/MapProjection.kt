package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

interface MapProjection : Projection<DoubleVector> {
    val mapRect: DoubleRectangle
}