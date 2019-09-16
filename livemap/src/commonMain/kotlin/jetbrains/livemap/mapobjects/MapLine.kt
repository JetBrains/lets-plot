package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.values.Color
import jetbrains.livemap.projections.LonLatPoint

class MapLine(
    index: Int,
    mapId: String?,
    regionId: String?,

    val lineDash: List<Double>,
    val strokeColor: Color,
    val strokeWidth: Double,
    val point: LonLatPoint
) : MapObject(index, mapId, regionId)