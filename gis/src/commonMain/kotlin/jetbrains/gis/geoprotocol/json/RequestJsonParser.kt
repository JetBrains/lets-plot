package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.gis.common.json.FluentJsonObject
import jetbrains.gis.common.json.JsonArray
import jetbrains.gis.common.json.JsonObject
import jetbrains.gis.common.json.JsonUtils.stringStreamOf
import jetbrains.gis.geoprotocol.*
import jetbrains.gis.geoprotocol.GeoRequest.FeatureOption
import jetbrains.gis.geoprotocol.GeoRequest.GeocodingSearchRequest.AmbiguityResolver.IgnoringStrategy
import jetbrains.gis.geoprotocol.json.RequestKeys.AMBIGUITY_BOX
import jetbrains.gis.geoprotocol.json.RequestKeys.AMBIGUITY_CLOSEST_COORD
import jetbrains.gis.geoprotocol.json.RequestKeys.AMBIGUITY_IGNORING_STRATEGY
import jetbrains.gis.geoprotocol.json.RequestKeys.AMBIGUITY_RESOLVER
import jetbrains.gis.geoprotocol.json.RequestKeys.COORDINATE_LAT
import jetbrains.gis.geoprotocol.json.RequestKeys.COORDINATE_LON
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
import jetbrains.gis.geoprotocol.json.RequestKeys.REGION_QUERIES
import jetbrains.gis.geoprotocol.json.RequestKeys.REGION_QUERY_NAMES
import jetbrains.gis.geoprotocol.json.RequestKeys.REGION_QUERY_PARENT
import jetbrains.gis.geoprotocol.json.RequestKeys.RESOLUTION
import jetbrains.gis.geoprotocol.json.RequestKeys.REVERSE_COORDINATES
import jetbrains.gis.geoprotocol.json.RequestKeys.REVERSE_LEVEL
import jetbrains.gis.geoprotocol.json.RequestKeys.REVERSE_PARENT
import jetbrains.gis.geoprotocol.json.RequestKeys.TILES
import kotlin.math.abs

object RequestJsonParser {

    fun parse(requestJson: JsonObject): GeoRequest {
        val requestFluentJson = FluentJsonObject(requestJson)

        return when (requestFluentJson.getEnum(MODE, GeocodingMode.values())) {
            GeocodingMode.BY_ID -> parseExplicitRequest(requestFluentJson)
            GeocodingMode.BY_NAME -> parseGeocodingRequest(requestFluentJson)
            GeocodingMode.REVERSE -> parseReverseGeocodingRequest(requestFluentJson)
        }
    }

    private fun <T : GeoRequestBuilder.RequestBuilderBase<*>> parseCommon(
        requestFluentJson: FluentJsonObject,
        builder: T
    ) {
        requestFluentJson
            .getOptionalInt(RESOLUTION, builder::setResolution)
            .forEnums(FEATURE_OPTIONS, builder::addFeature, FeatureOption.values())
            .getExistingObject(TILES) { tiles ->
                tiles
                .forEntries { id, quadKeys ->
                    builder.addTiles(id, stringStreamOf(quadKeys as JsonArray).map { key -> QuadKey(key!!) }.toList())
                }
            }
    }

    private fun parseExplicitRequest(requestFluentJson: FluentJsonObject): GeoRequest {
        val builder = GeoRequestBuilder.ExplicitRequestBuilder()

        requestFluentJson
            .accept { v -> parseCommon(v, builder) }
            .getStrings(IDS) { list -> builder.setIds(list.map { s -> s!! }) }

        return builder.build()
    }

    private fun parseGeocodingRequest(requestFluentJson: FluentJsonObject): GeoRequest {
        val builder = GeoRequestBuilder.GeocodingRequestBuilder()

        requestFluentJson
            .accept { v -> parseCommon(v, builder) }
            .getOptionalEnum(LEVEL, builder::setLevel, FeatureLevel.values())
            .getInt(NAMESAKE_EXAMPLE_LIMIT, builder::setNamesakeExampleLimit)
            .forObjects(REGION_QUERIES) { query ->
                val regionQueryBuilder = GeoRequestBuilder.RegionQueryBuilder()

                query
                    .getStrings(REGION_QUERY_NAMES, regionQueryBuilder::setQueryNames)
                    .getExistingObject(AMBIGUITY_RESOLVER) { resolver ->
                        resolver
                            .getExistingObject(AMBIGUITY_BOX) { jsonBox ->
                                parseRect(
                                    jsonBox,
                                    regionQueryBuilder::setBox
                                )
                            }
                            .getExistingArray(AMBIGUITY_CLOSEST_COORD) { jsonCoord ->
                                regionQueryBuilder
                                    .setClosestObject(
                                        DoubleVector(
                                            jsonCoord.getDouble(COORDINATE_LON),
                                            jsonCoord.getDouble(COORDINATE_LAT)
                                        )
                                    )
                            }
                            .getOptionalEnum(
                                AMBIGUITY_IGNORING_STRATEGY,
                                regionQueryBuilder::setIgnoringStrategy,
                                IgnoringStrategy.values()
                            )
                    }
                    .getExistingObject(REGION_QUERY_PARENT) { parent ->
                        parseMapRegion(
                            parent,
                            regionQueryBuilder::setParent
                        )
                    }

                builder.addQuery(regionQueryBuilder.build())
            }

        return builder.build()
    }

    private fun parseRect(jsonBox: FluentJsonObject, setter: (DoubleRectangle?) -> Unit) {
        val lonMin = jsonBox.getDouble(LON_MIN)
        val latMin = jsonBox.getDouble(LAT_MIN)
        val lonMax = jsonBox.getDouble(LON_MAX)
        val latMax = jsonBox.getDouble(LAT_MAX)

        setter(
            DoubleRectangle(
                lonMin,
                latMin,
                abs(lonMax - lonMin),
                abs(latMax - latMin)
            )
        )
    }

    private fun parseReverseGeocodingRequest(requestFluentJson: FluentJsonObject): GeoRequest {
        val builder = GeoRequestBuilder.ReverseGeocodingRequestBuilder()

        requestFluentJson
            .accept { v -> parseCommon(v, builder) }
            .getEnum(REVERSE_LEVEL, builder::setLevel, FeatureLevel.values())
            .getExistingObject(REVERSE_PARENT) { obj -> parseMapRegion(obj, builder::setParent) }
            .getArray(REVERSE_COORDINATES) { coordinates ->
                builder
                    .setCoordinates(coordinates.stream()
                        .map { jsonValue -> jsonValue as JsonArray }
                        .map { coord ->
                        DoubleVector(
                            coord.getDouble(COORDINATE_LON),
                            coord.getDouble(COORDINATE_LAT)
                        )
                    }
                    )
            }

        return builder.build()
    }

    private fun parseMapRegion(json: FluentJsonObject, setter: (MapRegion?) -> Unit) {
        val mapRegion = GeoRequestBuilder.MapRegionBuilder()
        json
            .getStrings(MAP_REGION_VALUES) { list -> mapRegion.setParentValues(list.map { s -> s!! }) }
            .getBoolean(MAP_REGION_KIND, mapRegion::setParentKind)

        setter(mapRegion.build())
    }
}

private fun <E> ArrayList<E>.getDouble(index: Int) = get(index) as Double
