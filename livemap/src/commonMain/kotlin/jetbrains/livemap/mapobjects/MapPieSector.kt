package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.values.Color

class MapPieSector(
    index: Int,
    mapId: String?,
    regionId: String?,

    val point: Vec<LonLat>,
    val radius: Double,
    val startAngle: Double,
    val endAngle: Double,

    val fillColor: Color,
    val strokeColor: Color,
    val strokeWidth: Double

) : MapObject(index, mapId, regionId)