package jetbrains.livemap.projections

import jetbrains.datalore.base.projectionGeometry.LonLatRectangle
import jetbrains.datalore.base.projectionGeometry.Typed

interface Geographic

typealias GeographicPoint = Typed.Point<Geographic>

internal interface GeoProjection : Transform<LonLatPoint, GeographicPoint> {
    fun validRect(): LonLatRectangle
}