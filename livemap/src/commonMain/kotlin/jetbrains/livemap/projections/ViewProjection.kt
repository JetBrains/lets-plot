package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

interface ViewProjection {

    val viewSize: DoubleVector

    var center: DoubleVector

    var zoom: Int

    val visibleCells: Set<CellKey>
    val viewRect: DoubleRectangle

    fun getViewX(mapX: Double): Double
    fun getViewY(mapY: Double): Double

    fun getMapX(viewX: Double): Double
    fun getMapY(viewY: Double): Double
    fun getOrigins(viewOrigin: DoubleVector, viewDimension: DoubleVector): List<DoubleVector>

    fun getMapCoord(viewCoord: DoubleVector): DoubleVector {
        return DoubleVector(getMapX(viewCoord.x), getMapY(viewCoord.y))
    }

    fun getViewCoord(mapCoord: DoubleVector): DoubleVector {
        return DoubleVector(getViewX(mapCoord.x), getViewY(mapCoord.y))
    }

    companion object {
        fun create(helper: ViewProjectionHelper, viewSize: DoubleVector, viewCenter: DoubleVector): ViewProjection {
            return ViewProjectionImpl(helper, viewSize, viewCenter)
        }
    }
}