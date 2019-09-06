package jetbrains.datalore.maps.cell.mapobjects

import jetbrains.datalore.base.values.Color
import jetbrains.gis.geoprotocol.Geometry
import jetbrains.livemap.mapobjects.MapObject

class MapPath (
    index: Int,
    mapId: String?,
    regionId: String?,

    val animation: Int,
    val speed: Double,
    val flow: Double,

    val lineDash: List<Double>,
    val strokeColor: Color,
    val strokeWidth: Double,
    val geometry: Geometry
    //val arrowSpec: ArrowSpec,

) : MapObject(index, mapId, regionId)
