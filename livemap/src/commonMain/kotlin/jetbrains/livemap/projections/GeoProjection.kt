package jetbrains.livemap.projections

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.Vec

interface Geographic

typealias GeographicPoint = Vec<Geographic>

internal interface GeoProjection : Transform<LonLatPoint, GeographicPoint> {
    fun validRect(): Rect<LonLat>
}