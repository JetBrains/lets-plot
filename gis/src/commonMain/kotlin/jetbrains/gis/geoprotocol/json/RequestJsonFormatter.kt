package jetbrains.gis.protocol.json

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.gis.common.json.FluentJsonArray
import jetbrains.gis.common.json.FluentJsonObject
import jetbrains.gis.common.json.FluentJsonValue
import jetbrains.gis.common.json.JsonUtils
import jetbrains.gis.common.json.JsonUtils.toJsonValue
import jetbrains.gis.protocol.GeoRequest
import jetbrains.gis.protocol.GeoRequest.ExplicitSearchRequest
import jetbrains.gis.protocol.GeoRequest.GeocodingSearchRequest
import jetbrains.gis.protocol.GeoRequest.ReverseGeocodingSearchRequest
import jetbrains.gis.protocol.GeocodingMode
import jetbrains.gis.protocol.GeocodingMode.*
import jetbrains.gis.protocol.MapRegion
import java.util.Collections
import java.util.Optional
import java.util.function.Function
import java.util.stream.Collectors.toList

object RequestJsonFormatter {
    private val PARENT_KIND_ID = true
    private val PARENT_KIND_NAME = false

    fun format(request: GeoRequest): JsonObject {
        if (request is GeocodingSearchRequest) {
            return geocoding(request as GeocodingSearchRequest)
        } else if (request is ExplicitSearchRequest) {
            return explicit(request as ExplicitSearchRequest)
        } else if (request is ReverseGeocodingSearchRequest) {
            return reverse(request as ReverseGeocodingSearchRequest)
        }

        throw IllegalStateException("Unknown request: " + request.getClass().getSimpleName())
    }

    private fun geocoding(request: GeocodingSearchRequest): JsonObject {
        return common(request, BY_NAME)
            .put(LEVEL, request.getLevel())
            .put(NAMESAKE_EXAMPLE_LIMIT, request.getNamesakeExampleLimit())
            .put(REGION_QUERIES, FluentJsonArray()
                .addAll(request.getQueries().stream().map(
                    { regionQuery ->
                        FluentJsonObject()
                            .put(REGION_QUERY_NAMES, toJsonValue(regionQuery.getNames()))
                            .put(REGION_QUERY_PARENT, formatMapRegion(regionQuery.getParent()))
                            .put(
                                AMBIGUITY_RESOLVER, FluentJsonObject()
                                    .put(
                                        AMBIGUITY_IGNORING_STRATEGY,
                                        regionQuery.getAmbiguityResolver().getIgnoringStrategy()
                                    )
                                    .put(
                                        AMBIGUITY_CLOSEST_COORD,
                                        formatCoord(regionQuery.getAmbiguityResolver().getClosestCoord())
                                    )
                                    .put(AMBIGUITY_BOX, formatRect(regionQuery.getAmbiguityResolver().getBox()))
                            )
                    }
                ).collect(toList<T>())
                )
            )
            .get()
    }

    private fun explicit(request: ExplicitSearchRequest): JsonObject {
        return common(request, BY_ID)
            .put(IDS, toJsonValue(request.getIds()))
            .get()
    }

    private fun reverse(request: ReverseGeocodingSearchRequest): JsonObject {
        return common(request, REVERSE)
            .put(REVERSE_PARENT, formatMapRegion(request.getParent()))
            .put(
                REVERSE_COORDINATES, FluentJsonArray()
                    .addAll(
                        request.getCoordinates().stream()
                            .map(??? { formatCoord() })
        .collect(toList<T>())
        )
        )
        .put(REVERSE_LEVEL, request.getLevel())
            .get()
    }

    private fun formatRect(rect: Optional<DoubleRectangle>): JsonValue {
        return rect.map(Function<DoubleRectangle, JsonValue> { formatRect(it) }).orElse(JsonUtils.NULL)
    }

    private fun formatRect(rect: DoubleRectangle): JsonValue {
        return FluentJsonObject()
            .put(LON_MIN, rect.getLeft())
            .put(LAT_MIN, Math.min(rect.getTop(), rect.getBottom()))
            .put(LON_MAX, rect.getRight())
            .put(LAT_MAX, Math.max(rect.getTop(), rect.getBottom()))
            .get()
    }

    private fun formatCoord(coord: Optional<DoubleVector>): JsonValue {
        return coord.map({ v -> formatCoord(v).get() }).orElse(JsonUtils.NULL)
    }

    private fun formatCoord(coord: DoubleVector): FluentJsonValue {
        return FluentJsonArray()
            .add(JsonNumber(coord.x))
            .add(JsonNumber(coord.y))
    }

    private fun common(request: GeoRequest, mode: GeocodingMode): FluentJsonObject {
        return FluentJsonObject()
            .put(VERSION, PROTOCOL_VERSION)
            .put(MODE, mode)
            .put(
                RESOLUTION,
                request.getLevelOfDetails().map({ v -> JsonNumber(v.toResolution()) }).orElse(JsonUtils.NULL)
            )
            .putRemovable(TILES, request.getTiles().map(???({ JsonUtils.toJsonObject() })).orElse(JsonUtils.NULL))
        .put(FEATURE_OPTIONS, toJsonValue(request.getFeatures()))
    }

    private fun <T> toJsonArray(objects: Iterable<T>, getObjectKey: Function<T, JsonValue>): JsonArray {
        val jsonArray = JsonArray()
        objects.forEach { `object` -> jsonArray.add(getObjectKey.apply(`object`)) }
        return jsonArray
    }

    private fun formatMapRegion(v: Optional<MapRegion>): JsonValue {
        return v
            .map({ mapRegion ->
                val kind = if (mapRegion.containsId())
                    PARENT_KIND_ID
                else
                    PARENT_KIND_NAME

                val values = if (mapRegion.containsId())
                    mapRegion.getIdList()
                else
                    listOf(mapRegion.getName())

                FluentJsonObject()
                    .put(MAP_REGION_KIND, kind)
                    .put(MAP_REGION_VALUES, toJsonArray(values, Function<String, JsonValue> { JsonString() }))
                    .get()
            })
            .orElse(JsonUtils.NULL)
    }
}
