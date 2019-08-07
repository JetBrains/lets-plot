package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

internal interface GeoProjection : Projection<DoubleVector> {
    fun validRect(): DoubleRectangle
}