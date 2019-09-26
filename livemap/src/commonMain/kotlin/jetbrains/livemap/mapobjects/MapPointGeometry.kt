package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Vec

interface MapPointGeometry {
    val point: Vec<LonLat>
}