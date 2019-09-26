package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.values.Color

class MapLine(
    index: Int,
    mapId: String?,
    regionId: String?,

    override val point: Vec<LonLat>,

    val lineDash: List<Double>,
    val strokeColor: Color,
    val strokeWidth: Double
) : MapObject(index, mapId, regionId), MapPointGeometry