package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Vec

class MapHeatmap(
    index: Int,
    mapId: String?,
    regionId: String?,

    override val point: Vec<LonLat>

) : MapObject(index, mapId, regionId), MapPointGeometry