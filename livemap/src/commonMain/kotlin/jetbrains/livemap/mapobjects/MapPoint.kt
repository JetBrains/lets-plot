package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.values.Color

class MapPoint(
    index: Int,
    mapId: String?,
    regionId: String?,

    override var point: Vec<LonLat>,

    val label: String,
    val animation: Int,

    val shape: Int,
    val radius: Double,
    val fillColor: Color,
    val strokeColor: Color,
    val strokeWidth: Double
) : MapObject(index, mapId, regionId), MapPointGeometry
