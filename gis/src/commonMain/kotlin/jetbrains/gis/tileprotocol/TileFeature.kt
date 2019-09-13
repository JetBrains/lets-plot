package jetbrains.gis.tileprotocol

import jetbrains.datalore.base.projectionGeometry.*
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
            fun <ProjT> createMultiPoint(multiPoint: Typed.MultiPoint<ProjT>): TileGeometry {
                return TileGeometry(MULTI_POINT, multiPoint.reinterpret(), null, null)
            }

            fun <ProjT> createMultiLineString(multiLineString: Typed.MultiLineString<ProjT>): TileGeometry {
                return TileGeometry(MULTI_LINESTRING, null, multiLineString.reinterpret(), null)
            }

            fun <ProjT> createMultiPolygon(multiPolygon: Typed.MultiPolygon<ProjT>): TileGeometry {
                return TileGeometry(MULTI_POLYGON, null, null, multiPolygon.reinterpret())
            }
        }
    }

    companion object {
        private const val CLASS = "class"
        private const val SUB = "sub"
    }
}
