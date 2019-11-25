/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.projectionGeometry.Generic
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.gis.common.json.*
import jetbrains.gis.geoprotocol.Boundary
import jetbrains.gis.geoprotocol.GeoResponse
import jetbrains.gis.geoprotocol.GeoResponse.*
import jetbrains.gis.geoprotocol.GeoTile
import jetbrains.gis.geoprotocol.json.ResponseKeys.BOUNDARY
import jetbrains.gis.geoprotocol.json.ResponseKeys.CENTROID
import jetbrains.gis.geoprotocol.json.ResponseKeys.DATA
import jetbrains.gis.geoprotocol.json.ResponseKeys.FEATURES
import jetbrains.gis.geoprotocol.json.ResponseKeys.HIGHLIGHTS
import jetbrains.gis.geoprotocol.json.ResponseKeys.ID
import jetbrains.gis.geoprotocol.json.ResponseKeys.LAT
import jetbrains.gis.geoprotocol.json.ResponseKeys.LEVEL
import jetbrains.gis.geoprotocol.json.ResponseKeys.LIMIT
import jetbrains.gis.geoprotocol.json.ResponseKeys.LON
import jetbrains.gis.geoprotocol.json.ResponseKeys.MESSAGE
import jetbrains.gis.geoprotocol.json.ResponseKeys.NAME
import jetbrains.gis.geoprotocol.json.ResponseKeys.NAMESAKE_COUNT
import jetbrains.gis.geoprotocol.json.ResponseKeys.NAMESAKE_EXAMPLES
import jetbrains.gis.geoprotocol.json.ResponseKeys.NAMESAKE_NAME
import jetbrains.gis.geoprotocol.json.ResponseKeys.NAMESAKE_PARENTS
import jetbrains.gis.geoprotocol.json.ResponseKeys.POSITION
import jetbrains.gis.geoprotocol.json.ResponseKeys.QUERY
import jetbrains.gis.geoprotocol.json.ResponseKeys.STATUS
import jetbrains.gis.geoprotocol.json.ResponseKeys.TILES

object ResponseJsonFormatter {
    fun format(response: GeoResponse): Obj {
        if (response is SuccessGeoResponse) {
            return success(response)
        }

        if (response is AmbiguousGeoResponse) {
            return ambiguous(response)
        }

        return if (response is ErrorGeoResponse) {
            error(response)
        } else error(ErrorGeoResponse("Unknown response: " + response::class.toString()))

    }

    private fun success(response: SuccessGeoResponse): Obj {
        return FluentObject()
            .put(STATUS, ResponseStatus.SUCCESS)
            .put(MESSAGE, "OK")
            .put(DATA,
                FluentObject()
                    .put(LEVEL, response.featureLevel)
                    .put(FEATURES, FluentArray()
                        .addAll(response.features.map { feature ->
                            FluentObject()
                                .put(QUERY, feature.request)
                                .put(ID, feature.id)
                                .put(NAME, feature.name)
                                .putRemovable(HIGHLIGHTS, stringArray(feature.highlights))
                                .putRemovable(LIMIT, formatRect(feature.limit))
                                .putRemovable(POSITION, formatRect(feature.position))
                                .putRemovable(CENTROID, formatPoint(feature.centroid))
                                .putRemovable(BOUNDARY, formatGeometry(feature.boundary))
                                .putRemovable(TILES, formatTiles(feature.tiles))
                        })
                    )
            )
            .get()
    }

    private fun error(response: ErrorGeoResponse): Obj {
        return FluentObject()
            .put(STATUS, ResponseStatus.ERROR)
            .put(MESSAGE, response.message)
            .get()
    }

    private fun ambiguous(response: AmbiguousGeoResponse): Obj {
        return FluentObject()
            .put(STATUS, ResponseStatus.AMBIGUOUS)
            .put(MESSAGE, "Ambiguous")
            .put(DATA, FluentObject()
                .put(LEVEL, response.featureLevel)
                .put(FEATURES, FluentArray()
                    .addAll(response.features.map { feature ->
                        FluentObject()
                            .put(QUERY, feature.request)
                            .put(NAMESAKE_COUNT, feature.namesakeCount)
                            .put(NAMESAKE_EXAMPLES, FluentArray()
                                .addAll(feature.namesakes.map { namesake ->
                                    FluentObject()
                                        .put(NAMESAKE_NAME, namesake.name)
                                        .put(NAMESAKE_PARENTS, FluentArray()
                                            .addAll(namesake.parents.map { parent ->
                                                FluentObject()
                                                    .put(NAMESAKE_NAME, parent.name)
                                                    .put(LEVEL, parent.level)
                                            })
                                        )
                                })
                            )
                    })
                )
            ).get()
    }


    private fun stringArray(v: List<String>?): FluentArray? {
        return if (v == null || v.isEmpty()) null else FluentArray(v)
    }

    private fun formatRect(v: GeoRectangle?): FluentObject? {
        return when (v) {
            null -> null
            else -> ProtocolJsonHelper.formatGeoRectangle(v)
        }
    }

    private fun formatPoint(v: Vec<Generic>?): FluentValue? {
        return v?.let { FluentObject()
            .put(LON, it.x)
            .put(LAT, it.y)
        }
    }

    private fun formatGeometry(geometry: Boundary<Generic>?): FluentPrimitive? {
        return geometry?.let { FluentPrimitive(geometryToString(it)) }
    }

    private fun geometryToString(geometry: Boundary<Generic>): String {
        return StringGeometries.getRawData(geometry)
    }

    private fun formatTiles(tiles: List<GeoTile>?): FluentObject? {
        return tiles?.let {
            val obj = FluentObject()
            for (tile in tiles) {
                val geometries = FluentArray()

                for (boundary in tile.geometries) {
                    geometries.add(geometryToString(boundary))
                }

                obj.put(tile.key.key, geometries)
            }

            return obj
        }
    }

}
