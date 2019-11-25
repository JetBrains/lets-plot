/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.common.json.*
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

    fun format(request: GeoRequest): Obj {
        return when (request) {
            is GeocodingSearchRequest -> geocoding(request)
            is ExplicitSearchRequest -> explicit(request)
            is ReverseGeocodingSearchRequest -> reverse(request)
            else -> throw IllegalStateException("Unknown request: " + request::class.toString())
        }

    }

    private fun geocoding(request: GeocodingSearchRequest): Obj {
        return common(request, BY_NAME)
            .put(LEVEL, request.level)
            .put(NAMESAKE_EXAMPLE_LIMIT, request.namesakeExampleLimit)
            .put(REGION_QUERIES, FluentArray()
                .addAll(request.queries.map { regionQuery ->
                    FluentObject()
                        .put(REGION_QUERY_NAMES, regionQuery.names)
                        .put(REGION_QUERY_PARENT, formatMapRegion(regionQuery.parent))
                        .put(AMBIGUITY_RESOLVER, FluentObject()
                            .put(AMBIGUITY_IGNORING_STRATEGY, regionQuery.ambiguityResolver.ignoringStrategy)
                            .put(AMBIGUITY_CLOSEST_COORD, formatCoord(regionQuery.ambiguityResolver.closestCoord))
                            .put(AMBIGUITY_BOX, regionQuery.ambiguityResolver.box?.let { formatRect(it) })
                        )
                })
            )
            .get()
    }

    private fun explicit(request: ExplicitSearchRequest): Obj {
        return common(request, BY_ID)
            .put(IDS, request.ids)
            .get()
    }

    private fun reverse(request: ReverseGeocodingSearchRequest): Obj {
        return common(request, REVERSE)
            .put(REVERSE_PARENT, formatMapRegion(request.parent))
            .put(REVERSE_COORDINATES, FluentArray()
                .addAll(request.coordinates.map {
                    FluentArray()
                        .add(it.x)
                        .add(it.y)
                })
            )
            .put(REVERSE_LEVEL, request.level)
            .get()
    }

    private fun formatRect(rect: DoubleRectangle): FluentObject? =
        FluentObject()
            .put(LON_MIN, rect.left)
            .put(LAT_MIN, min(rect.top, rect.bottom))
            .put(LON_MAX, rect.right)
            .put(LAT_MAX, max(rect.top, rect.bottom))

    private fun formatCoord(coord: DoubleVector?): FluentArray? = coord?.let {
        FluentArray()
            .add(it.x)
            .add(it.y)
    }

    private fun common(request: GeoRequest, mode: GeocodingMode): FluentObject =
        FluentObject()
            .put(VERSION, PROTOCOL_VERSION)
            .put(MODE, mode)
            .put(RESOLUTION, request.levelOfDetails?.toResolution())
            .put(FEATURE_OPTIONS, request.features.map { formatEnum(it) })
            .putRemovable(TILES, request.tiles?.let {
                    val obj = FluentObject()
                    it.map { (region, quads) ->
                        obj.put(region, quads.map(QuadKey<LonLat>::key))
                    }
                    obj
                }
            )


    private fun formatMapRegion(v: MapRegion?): FluentObject? {
        return v?.let { mapRegion ->
                val kind = if (mapRegion.containsId())
                    PARENT_KIND_ID
                else
                    PARENT_KIND_NAME

                val values = if (mapRegion.containsId())
                    mapRegion.idList
                else
                    listOf(mapRegion.name)

                FluentObject()
                    .put(MAP_REGION_KIND, kind)
                    .put(MAP_REGION_VALUES, values)
        }
    }
}
