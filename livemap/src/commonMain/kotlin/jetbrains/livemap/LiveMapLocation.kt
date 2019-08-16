package jetbrains.livemap

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoUtils.FULL_LONGITUDE
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ViewProjection
import kotlin.math.pow
import kotlin.math.round

class LiveMapLocation(private val myViewProjection: ViewProjection, private val myMapProjection: MapProjection) {

    val viewLonLatRect: DoubleRectangle
        get() {
            val viewRect = myViewProjection.viewRect

            val nw = worldToLonLat(viewRect.origin)
            val se = worldToLonLat(viewRect.origin.add(viewRect.dimension))

            return DoubleRectangle(nw.x, se.y, se.x - nw.x, nw.y - se.y)
        }

    private fun worldToLonLat(worldCoord: DoubleVector): DoubleVector {
        var coord = worldCoord
        val mapSize = myMapProjection.mapRect.dimension

        var shift = DoubleVector.ZERO

        if (worldCoord.x > mapSize.x) {
            shift = DoubleVector(FULL_LONGITUDE, shift.y)
            coord = DoubleVector(worldCoord.x - mapSize.x, worldCoord.y)
        } else if (worldCoord.x < 0) {
            shift = DoubleVector(-FULL_LONGITUDE, shift.y)
            coord = DoubleVector(mapSize.x + worldCoord.x, worldCoord.y)
        }

        return shift.add(myMapProjection.invert(coord))
    }

    companion object {

        fun getLocationString(viewRect: DoubleRectangle): String {
            val delta = viewRect.dimension.mul(0.05)

            return ("location = ["
                    + (viewRect.left + delta.x).round(6) + ", "
                    + (viewRect.top + delta.y).round(6) + ", "
                    + (viewRect.right - delta.x).round(6) + ", "
                    + (viewRect.bottom - delta.y).round(6) + "]")
        }

        private fun Double.round(digits: Int): Double {
            val a = 10.0.pow(digits)

            return round(this * a) / a
        }
    }
}