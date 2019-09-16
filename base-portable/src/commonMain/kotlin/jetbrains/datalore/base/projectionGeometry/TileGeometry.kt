package jetbrains.datalore.base.projectionGeometry

enum class GeometryType {
    MULTI_POINT,
    MULTI_LINESTRING,
    MULTI_POLYGON;
}

class TileGeometry<TypeT> private constructor(
    val type: GeometryType,
    val multiPoint: MultiPoint<TypeT>?,
    val multiLineString: MultiLineString<TypeT>?,
    val multiPolygon: MultiPolygon<TypeT>?
) {
    companion object {
        fun <TypeT> createMultiPoint(multiPoint: MultiPoint<TypeT>): TileGeometry<TypeT> {
            return TileGeometry(
                GeometryType.MULTI_POINT,
                multiPoint,
                null,
                null
            )
        }

        fun <TypeT> createMultiLineString(multiLineString: MultiLineString<TypeT>): TileGeometry<TypeT> {
            return TileGeometry(
                GeometryType.MULTI_LINESTRING,
                null,
                multiLineString,
                null
            )
        }

        fun <TypeT> createMultiPolygon(multiPolygon: MultiPolygon<TypeT>): TileGeometry<TypeT> {
            return TileGeometry(
                GeometryType.MULTI_POLYGON,
                null,
                null,
                multiPolygon
            )
        }
    }
}