package jetbrains.gis.tileprotocol

import jetbrains.datalore.base.projectionGeometry.MultiLineString
import jetbrains.datalore.base.projectionGeometry.MultiPoint
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.gis.common.twkb.Twkb.GeometryType
import jetbrains.gis.common.twkb.Twkb.GeometryType.*


class TileFeature(
    val tileGeometry: TileGeometry,
    private val myKind: Int?,
    private val mySub: Int?,
    val label: String?,
    val short: String?
) {

    fun getFieldValue(key: String): Int {
        if (SUB.equals(key, ignoreCase = true)) {
            return mySub ?: throw IllegalStateException("sub is empty")
        } else if (CLASS.equals(key, ignoreCase = true)) {
            return myKind ?: throw IllegalStateException("kind is empty")
        }

        throw IllegalArgumentException("Unknown myKey kind: $key")
    }

    class TileGeometry private constructor(
        val type: GeometryType,
        val multiPoint: MultiPoint?,
        val multiLineString: MultiLineString?,
        val multiPolygon: MultiPolygon?
    ) {
        companion object {
            fun createMultiPoint(multiPoint: MultiPoint): TileGeometry {
                return TileGeometry(MULTI_POINT, multiPoint, null, null)
            }

            fun createMultiLineString(multiLineString: MultiLineString): TileGeometry {
                return TileGeometry(MULTI_LINESTRING, null, multiLineString, null)
            }

            fun createMultiPolygon(multiPolygon: MultiPolygon): TileGeometry {
                return TileGeometry(MULTI_POLYGON, null, null, multiPolygon)
            }
        }
    }

    companion object {
        private const val CLASS = "class"
        private const val SUB = "sub"
    }
}
