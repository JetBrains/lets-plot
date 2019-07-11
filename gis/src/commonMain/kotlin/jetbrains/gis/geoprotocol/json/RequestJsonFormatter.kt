package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.gis.common.json.FluentJsonArray
import jetbrains.gis.common.json.FluentJsonObject
import jetbrains.gis.common.json.FluentJsonValue
import jetbrains.gis.common.json.JsonObject
import jetbrains.gis.geoprotocol.GeoRequest
import jetbrains.gis.geoprotocol.GeoRequest.*
import jetbrains.gis.geoprotocol.GeocodingMode
import jetbrains.gis.geoprotocol.GeocodingMode.*
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.gis.geoprotocol.json.RequestKeys.AMBIGUITY_BOX
import jetbrains.gis.geoprotocol.json.RequestKeys.AMBIGUITY_CLOSEST_COORD
import jetbrains.gis.geoprotocol.json.RequestKeys.AMBIGUITY_IGNORING_STRATEGY
import jetbrains.gis.geoprotocol.json.RequestKeys.AMBIGUITY_RESOLVER
import jetbrains.gis.geoprotocol.json.RequestKeys.FEATURE_OPTIONS
import jetbrains.gis.geoprotocol.json.RequestKeys.IDS
import jetbrains.gis.geoprotocol.json.RequestKeys.LAT_MAX
import jetbrains.gis.geoprotocol.json.RequestKeys.LAT_MIN
import jetbrains.gis.geoprotocol.json.RequestKeys.LEVEL
import jetbrains.gis.geoprotocol.json.RequestKeys.LON_MAX
import jetbrains.gis.geoprotocol.json.RequestKeys.LON_MIN
import jetbrains.gis.geoprotocol.json.RequestKeys.MAP_REGION_KIND
import jetbrains.gis.geoprotocol.json.RequestKeys.MAP_REGION_VALUES
import jetbrains.gis.geoprotocol.json.RequestKeys.MODE
import jetbrains.gis.geoprotocol.json.RequestKeys.NAMESAKE_EXAMPLE_LIMIT
import jetbrains.gis.geoprotocol.json.RequestKeys.PROTOCOL_VERSION
import jetbrains.gis.geoprotocol.json.RequestKeys.REGION_QUERIES
import jetbrains.gis.geoprotocol.json.RequestKeys.REGION_QUERY_NAMES
import jetbrains.gis.geoprotocol.json.RequestKeys.REGION_QUERY_PARENT
import jetbrains.gis.geoprotocol.json.RequestKeys.RESOLUTION
import jetbrains.gis.geoprotocol.json.RequestKeys.REVERSE_COORDINATES
import jetbrains.gis.geoprotocol.json.RequestKeys.REVERSE_LEVEL
import jetbrains.gis.geoprotocol.json.RequestKeys.REVERSE_PARENT
import jetbrains.gis.geoprotocol.json.RequestKeys.TILES
import jetbrains.gis.geoprotocol.json.RequestKeys.VERSION
import kotlin.math.max
import kotlin.math.min

object RequestJsonFormatter {
    private const val PARENT_KIND_ID = true
    private const val PARENT_KIND_NAME = false

    fun format(request: GeoRequest): JsonObject {
        return when (request) {
            is GeocodingSearchRequest -> geocoding(request)
            is ExplicitSearchRequest -> explicit(request)
            is ReverseGeocodingSearchRequest -> reverse(request)
            else -> throw IllegalStateException("Unknown request: " + request::class.simpleName)
        }

    }

    private fun geocoding(request: GeocodingSearchRequest): JsonObject {
        return common(request, BY_NAME)
            .put(LEVEL, request.level)
            .put(NAMESAKE_EXAMPLE_LIMIT, request.namesakeExampleLimit)
            .put(REGION_QUERIES, FluentJsonArray()
                .addAll(request.queries.map { regionQuery ->
                    FluentJsonObject()
                        .put(REGION_QUERY_NAMES, regionQuery.names)
                        .put(REGION_QUERY_PARENT, formatMapRegion(regionQuery.parent))
                        .put(
                            AMBIGUITY_RESOLVER, FluentJsonObject()
                                .put(AMBIGUITY_IGNORING_STRATEGY, regionQuery.ambiguityResolver.ignoringStrategy)
                                .put(AMBIGUITY_CLOSEST_COORD, formatCoord(regionQuery.ambiguityResolver.closestCoord))
                                .put(AMBIGUITY_BOX, formatRect(regionQuery.ambiguityResolver.box))
                        )
                })
            )
            .get()
    }

    private fun explicit(request: ExplicitSearchRequest): JsonObject {
        return common(request, BY_ID)
            .put(IDS, request.ids)
            .get()
    }

    private fun reverse(request: ReverseGeocodingSearchRequest): JsonObject {
        return common(request, REVERSE)
            .put(REVERSE_PARENT, formatMapRegion(request.parent))
            .put(
                REVERSE_COORDINATES, FluentJsonArray()
                    .addAll(request.coordinates.map { formatCoord(it) })
            )
            .put(REVERSE_LEVEL, request.level)
            .get()
    }

    private fun formatRect(rect: DoubleRectangle?): Any? {
        return rect?.let { formatRect(it) }
    }

    private fun formatRect(rect: DoubleRectangle): Any? {
        return FluentJsonObject()
            .put(LON_MIN, rect.left)
            .put(LAT_MIN, min(rect.top, rect.bottom))
            .put(LON_MAX, rect.right)
            .put(LAT_MAX, max(rect.top, rect.bottom))
            .get()
    }

    private fun formatCoord(coord: DoubleVector?): Any? {
        return coord?.let { v -> formatCoord(v).get() }
    }

    private fun formatCoord(coord: DoubleVector): FluentJsonValue {
        return FluentJsonArray()
            .add(coord.x)
            .add(coord.y)
    }

    private fun common(request: GeoRequest, mode: GeocodingMode): FluentJsonObject {
        return FluentJsonObject()
            .put(VERSION, PROTOCOL_VERSION)
            .put(MODE, mode)
            .put(RESOLUTION, request.levelOfDetails?.let { v -> v.toResolution() })
            .putRemovable(TILES, request.tiles)
            .put(FEATURE_OPTIONS, request.features)
    }

    private fun formatMapRegion(v: MapRegion?): Any? {
        return v?.let { mapRegion ->
                val kind = if (mapRegion.containsId())
                    PARENT_KIND_ID
                else
                    PARENT_KIND_NAME

                val values = if (mapRegion.containsId())
                    mapRegion.idList
                else
                    listOf(mapRegion.name)

                FluentJsonObject()
                    .put(MAP_REGION_KIND, kind)
                    .put(MAP_REGION_VALUES, values)
                    .get()
        }
    }
}
