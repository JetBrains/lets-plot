package jetbrains.livemap.projections

import jetbrains.datalore.base.projectionGeometry.explicitVec

interface ViewProjection {

    val viewSize: ClientPoint

    var center: WorldPoint

    var zoom: Int

    val visibleCells: Set<CellKey>
    val viewRect: WorldRectangle

    fun getViewX(p: WorldPoint): Double
    fun getViewY(p: WorldPoint): Double

    fun getMapX(p: ClientPoint): Double
    fun getMapY(p: ClientPoint): Double
    fun getOrigins(viewOrigin: ClientPoint, viewDimension: ClientPoint): List<ClientPoint>

    fun getMapCoord(viewCoord: ClientPoint): WorldPoint {
        return explicitVec<World>(getMapX(viewCoord), getMapY(viewCoord))
    }

    fun getViewCoord(mapCoord: WorldPoint): ClientPoint {
        return explicitVec<Client>(getViewX(mapCoord), getViewY(mapCoord))
    }

    companion object {
        fun create(helper: ViewProjectionHelper, viewSize: ClientPoint, viewCenter: WorldPoint): ViewProjection {
            return ViewProjectionImpl(helper, viewSize, viewCenter)
        }
    }
}