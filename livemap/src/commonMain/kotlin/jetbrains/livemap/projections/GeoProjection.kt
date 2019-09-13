package jetbrains.livemap.projections

import jetbrains.datalore.base.projectionGeometry.LonLatRectangle
import jetbrains.datalore.base.projectionGeometry.Point

internal interface GeoProjection : Transform<LonLatPoint, Point> {
    fun validRect(): LonLatRectangle
}