package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color

class MapLine(
    index: Int,
    mapId: String?,
    regionId: String?,

    val lineDash: List<Double>,
    val strokeColor: Color,
    val strokeWidth: Double,
    val point: DoubleVector
) : MapObject(index, mapId, regionId)