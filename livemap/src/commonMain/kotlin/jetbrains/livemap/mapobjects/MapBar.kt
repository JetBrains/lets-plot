package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.projections.Client

class MapBar(
    index: Int,
    mapId: String?,
    regionId: String?,

    val point: Vec<LonLat>,

    val fillColor: Color,
    val strokeColor: Color,
    val strokeWidth: Double,
    val barRadius: Vec<Client>,
    val centerOffset: Vec<Client>
) : MapObject(index, mapId, regionId)