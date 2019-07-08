package jetbrains.gis.protocol.json

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.json.JsonArray
import jetbrains.datalore.base.json.JsonObject
import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.gis.common.json.FluentJsonObject
import jetbrains.gis.common.json.JsonUtils.stringStreamOf
import jetbrains.gis.protocol.FeatureLevel
import jetbrains.gis.protocol.GeoRequest
import jetbrains.gis.protocol.GeoRequest.FeatureOption
import jetbrains.gis.protocol.GeoRequest.GeocodingSearchRequest.AmbiguityResolver.IgnoringStrategy
import jetbrains.gis.protocol.GeoRequestBuilder.*
import jetbrains.gis.protocol.GeocodingMode
import jetbrains.gis.protocol.MapRegion
import java.util.Optional
import java.util.stream.Collectors.toList

object RequestJsonParser {

    fun parse(requestJson: JsonObject): GeoRequest {
        val requestFluentJson = FluentJsonObject(requestJson)

        when (requestFluentJson.getEnum(MODE, GeocodingMode.values())) {
            BY_ID -> return parseExplicitRequest(requestFluentJson)

            BY_NAME -> return parseGeocodingRequest(requestFluentJson)

            REVERSE -> return parseReverseGeocodingRequest(requestFluentJson)

            else -> throw IllegalStateException("Unknown geocoding mode")
        }
    }

    private fun <T : RequestBuilderBase<*>> parseCommon(requestFluentJson: FluentJsonObject, builder: T) {
        requestFluentJson
            .getOptionalInt(RESOLUTION, ???({ builder.setResolution() }))
        .getExistingObject(
            TILES
        ) { tiles ->
            tiles
                .forEntries { id, quadKeys ->
                    builder
                        .addTiles(
                            id,
                            stringStreamOf(quadKeys as JsonArray)
                                .map(???({ QuadKey() }))
                    .collect(toList<T>())
                    )
                }
        }
            .forEnums(FEATURE_OPTIONS, ???({ builder.addFeature() }), FeatureOption.values())
    }

    private fun parseExplicitRequest(requestFluentJson: FluentJsonObject): GeoRequest {
        val builder = ExplicitRequestBuilder()

        requestFluentJson
            .accept({ v -> parseCommon(v, builder) })
            .getStrings(IDS, ???({ builder.setIds() }))

        return builder.build()
    }

    private fun parseGeocodingRequest(requestFluentJson: FluentJsonObject): GeoRequest {
        val builder = GeocodingRequestBuilder()

        requestFluentJson
            .accept({ v -> parseCommon(v, builder) })
            .getOptionalEnum(LEVEL, ???({ builder.setLevel() }), FeatureLevel.values())
        .getInt(NAMESAKE_EXAMPLE_LIMIT, ???({ builder.setNamesakeExampleLimit() }))
        .forObjects(REGION_QUERIES) { query ->
            val regionQueryBuilder = RegionQueryBuilder()

            query
                .getStrings(REGION_QUERY_NAMES, ???({ regionQueryBuilder.setQueryNames() }))
            .getExistingObject(AMBIGUITY_RESOLVER) { resolver ->
            resolver
                .getExistingObject(AMBIGUITY_BOX) { jsonBox ->
                    parseRect(
                        jsonBox,
                        Consumer<Optional<DoubleRectangle>> { regionQueryBuilder.setBox() })
                }
                .getExistingArray(AMBIGUITY_CLOSEST_COORD, { jsonCoord ->
                    regionQueryBuilder.setClosestObject(
                        Optional.of(
                            DoubleVector(
                                jsonCoord.getDouble(COORDINATE_LON),
                                jsonCoord.getDouble(COORDINATE_LAT)
                            )
                        )
                    )
                }
                )
                .getOptionalEnum(AMBIGUITY_IGNORING_STRATEGY, ???({ regionQueryBuilder.setIgnoringStrategy() }), IgnoringStrategy.values())
        }
            .getExistingObject(
                REGION_QUERY_PARENT,
                { parent -> parseMapRegion(parent, Consumer<Optional<MapRegion>> { regionQueryBuilder.setParent() }) })
            builder.addQuery(regionQueryBuilder.build())
        }

        return builder.build()
    }

    private fun parseRect(jsonBox: FluentJsonObject, setter: Consumer<Optional<DoubleRectangle>>) {
        val lonMin = jsonBox.getDouble(LON_MIN)
        val latMin = jsonBox.getDouble(LAT_MIN)
        val lonMax = jsonBox.getDouble(LON_MAX)
        val latMax = jsonBox.getDouble(LAT_MAX)

        setter.accept(
            Optional.of(
                DoubleRectangle(
                    lonMin,
                    latMin,
                    Math.abs(lonMax - lonMin),
                    Math.abs(latMax - latMin)
                )
            )
        )
    }

    private fun parseReverseGeocodingRequest(requestFluentJson: FluentJsonObject): GeoRequest {
        val builder = ReverseGeocodingRequestBuilder()

        requestFluentJson
            .accept({ v -> parseCommon(v, builder) })
            .getEnum(REVERSE_LEVEL, ???({ builder.setLevel() }), FeatureLevel.values())
        .getExistingObject(REVERSE_PARENT) { obj ->
            parseMapRegion(
                obj,
                Consumer<Optional<MapRegion>> { builder.setParent() })
        }
            .getArray(REVERSE_COORDINATES, { coordinates ->
                builder.setCoordinates(coordinates.stream()
                    .map({ jsonValue -> jsonValue as JsonArray })
                    .map({ coord ->
                        DoubleVector(
                            coord.getDouble(COORDINATE_LON),
                            coord.getDouble(COORDINATE_LAT)
                        )
                    }
                    )
                    .collect(toList<T>())
                )
            }
            )

        return builder.build()
    }

    private fun parseMapRegion(json: FluentJsonObject, setter: Consumer<Optional<MapRegion>>) {
        val mapRegion = MapRegionBuilder()
        json
            .getStrings(MAP_REGION_VALUES, ???({ mapRegion.setParentValues() }))
        .getBoolean(MAP_REGION_KIND, ???({ mapRegion.setParentKind() }))

        setter.accept(Optional.of(mapRegion.build()))
    }
}
