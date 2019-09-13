package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.values.Color
import jetbrains.livemap.entities.geometry.LonLatGeometry

class MapPolygon(
    index: Int,
    mapId: String?,
    regionId: String?,

    val lineDash: List<Double>,
    val strokeColor: Color,
    val strokeWidth: Double,
    val fillColor: Color,
    val geometry: LonLatGeometry?

) : MapObject(index, mapId, regionId)