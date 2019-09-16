package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.values.Color
import jetbrains.livemap.projections.LonLatPoint

class MapPoint(
    index: Int,
    mapId: String?,
    regionId: String?,

    val point: LonLatPoint,
    val label: String,
    val animation: Int,

    val shape: Int,
    val radius: Double,
    val fillColor: Color,
    val strokeColor: Color,
    val strokeWidth: Double
) : MapObject(index, mapId, regionId)
