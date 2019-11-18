/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.common.json.Arr
import jetbrains.gis.common.json.FluentObject
import jetbrains.gis.common.json.Obj
import jetbrains.gis.common.json.stringStreamOf
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

    fun parse(requestJson: Obj): GeoRequest {
        val requestFluentJson = FluentObject(requestJson)

        return when (requestFluentJson.getEnum(MODE, GeocodingMode.values())) {
            GeocodingMode.BY_ID -> parseExplicitRequest(requestFluentJson)
            GeocodingMode.BY_NAME -> parseGeocodingRequest(requestFluentJson)
            GeocodingMode.REVERSE -> parseReverseGeocodingRequest(requestFluentJson)
        }
    }

    private fun <T : GeoRequestBuilder.RequestBuilderBase<*>> parseCommon(
        requestFluentJson: FluentObject,
        builder: T
    ) {
        requestFluentJson
            .getOptionalInt(RESOLUTION) { builder.setResolution(it) }
            .forEnums(FEATURE_OPTIONS, { builder.addFeature(it) }, FeatureOption.values())
            .getExistingObject(TILES) { tiles -> tiles
                .forEntries { id, quadKeys ->
                    builder.addTiles(id, stringStreamOf(quadKeys as Arr).requireNoNulls().map(::QuadKey).toList())
                }
            }
    }

    private fun parseExplicitRequest(requestFluentJson: FluentObject): GeoRequest {
        val builder = GeoRequestBuilder.ExplicitRequestBuilder()

        requestFluentJson
            .accept { parseCommon(it, builder) }
            .getStrings(IDS) { builder.setIds(it.map { s -> s!! }) }

        return builder.build()
    }

    private fun parseGeocodingRequest(requestFluentJson: FluentObject): GeoRequest {
        val builder = GeoRequestBuilder.GeocodingRequestBuilder()

        requestFluentJson
            .accept { parseCommon(it, builder) }
            .getOptionalEnum(LEVEL, { builder.setLevel(it) }, FeatureLevel.values())
            .getInt(NAMESAKE_EXAMPLE_LIMIT) { builder.setNamesakeExampleLimit(it) }
            .forObjects(REGION_QUERIES) { query ->
                val queryBuilder = GeoRequestBuilder.RegionQueryBuilder()

                query
                    .getStrings(REGION_QUERY_NAMES) { queryBuilder.setQueryNames( it.map { s -> s!! })}
                    .getExistingObject(AMBIGUITY_RESOLVER) { resolver -> resolver
                        .getExistingObject(AMBIGUITY_BOX) { parseRect(it, queryBuilder::setBox) }
                        .getExistingArray(AMBIGUITY_CLOSEST_COORD) { queryBuilder
                            .setClosestObject( with (it) { DoubleVector(getDouble(COORDINATE_LON), getDouble(COORDINATE_LAT))})
                        }
                        .getOptionalEnum(AMBIGUITY_IGNORING_STRATEGY, { queryBuilder.setIgnoringStrategy(it) }, IgnoringStrategy.values())
                    }
                    .getExistingObject(REGION_QUERY_PARENT) { parseMapRegion(it, queryBuilder::setParent)}

                builder.addQuery(queryBuilder.build())
            }

        return builder.build()
    }

    private fun parseRect(jsonBox: FluentObject, setter: (DoubleRectangle?) -> Any) {
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

    private fun parseReverseGeocodingRequest(requestFluentJson: FluentObject): GeoRequest {
        val builder = GeoRequestBuilder.ReverseGeocodingRequestBuilder()

        requestFluentJson
            .accept { parseCommon(it, builder) }
            .getEnum(REVERSE_LEVEL, { builder.setLevel(it) }, FeatureLevel.values())
            .getExistingObject(REVERSE_PARENT) { parseMapRegion(it, builder::setParent) }
            .getArray(REVERSE_COORDINATES) { coordinates -> builder
                .setCoordinates(coordinates.stream()
                    .map { it as Arr }
                    .map { with (it) { DoubleVector(getDouble(COORDINATE_LON), getDouble(COORDINATE_LAT)) } }
                    .toList()
                )
            }

        return builder.build()
    }

    private fun parseMapRegion(json: FluentObject, setter: (MapRegion?) -> Any) {
        val mapRegion = GeoRequestBuilder.MapRegionBuilder()
        json
            .getStrings(MAP_REGION_VALUES) { mapRegion.setParentValues(it.map { s -> s!! }) }
            .getBoolean(MAP_REGION_KIND) { mapRegion.setParentKind(it) }

        setter(mapRegion.build())
    }
}

private fun <E> List<E>.getDouble(index: Int) = get(index) as Double
