package jetbrains.gis.tileprotocol

import jetbrains.datalore.base.projectionGeometry.MultiLineString
import jetbrains.datalore.base.projectionGeometry.MultiPoint
import jetbrains.datalore.base.projectionGeometry.Multipolygon
import jetbrains.gis.common.twkb.TWKB.GeometryType

import java.util.Optional

import jetbrains.gis.common.twkb.TWKB.GeometryType.MULTI_LINESTRING
import jetbrains.gis.common.twkb.TWKB.GeometryType.MULTI_POINT
import jetbrains.gis.common.twkb.TWKB.GeometryType.MULTI_POLYGON


class TileFeature(
    val tileGeometry: TileGeometry,
    private val myKind: Optional<Int>,
    private val mySub: Optional<Int>,
    val label: Optional<String>,
    val short: Optional<String>
) {

    fun getFieldValue(key: String): Int {
        if (SUB.equals(key, ignoreCase = true)) {
            return mySub.orElseThrow({ IllegalStateException("sub is empty") })
        } else if (CLASS.equals(key, ignoreCase = true)) {
            return myKind.orElseThrow({ IllegalStateException("kind is empty") })
        }

        throw IllegalArgumentException("Unknown key kind: $key")
    }

    class TileGeometry private constructor(
        val type: GeometryType,
        val multiPoint: Optional<MultiPoint>,
        val multiLineString: Optional<MultiLineString>,
        val multiPolygon: Optional<Multipolygon>
    ) {
        companion object {
            fun createMultiPoint(multiPoint: MultiPoint): TileGeometry {
                return TileGeometry(MULTI_POINT, Optional.of(multiPoint), Optional.empty(), Optional.empty())
            }

            fun createMultiLineString(multiLineString: MultiLineString): TileGeometry {
                return TileGeometry(MULTI_LINESTRING, Optional.empty(), Optional.of(multiLineString), Optional.empty())
            }

            fun createMultiPolygon(multiPolygon: Multipolygon): TileGeometry {
                return TileGeometry(MULTI_POLYGON, Optional.empty(), Optional.empty(), Optional.of(multiPolygon))
            }
        }
    }

    companion object {
        private val CLASS = "class"
        private val SUB = "sub"
    }
}
