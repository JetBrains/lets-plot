package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.gis.common.json.*
import jetbrains.gis.geoprotocol.GeoResponse
import jetbrains.gis.geoprotocol.GeoResponse.*
import jetbrains.gis.geoprotocol.GeoTile
import jetbrains.gis.geoprotocol.Geometry
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
    fun format(response: GeoResponse): JsonObject {
        if (response is SuccessGeoResponse) {
            return success(response)
        }

        if (response is AmbiguousGeoResponse) {
            return ambiguous(response)
        }

        return if (response is ErrorGeoResponse) {
            error(response)
        } else error(ErrorGeoResponse("Unknown response: " + response::class.simpleName))

    }

    private fun success(response: SuccessGeoResponse): JsonObject {
        return FluentJsonObject()
            .put(STATUS, ResponseStatus.SUCCESS)
            .put(MESSAGE, "OK")
            .put(
                DATA, FluentJsonObject()
                    .put(LEVEL, response.featureLevel)
                    .put(
                        FEATURES, FluentJsonArray()
                            .addAll(
                                response.features.map { feature ->
                                    FluentJsonObject()
                                        .put(QUERY, feature.request)
                                        .put(ID, feature.id)
                                        .put(NAME, feature.name)
                                        .putRemovable(HIGHLIGHTS, stringArray(feature.highlights))
                                        .putRemovable(LIMIT, formatRect(feature.limit))
                                        .putRemovable(POSITION, formatRect(feature.position))
                                        .putRemovable(CENTROID, formatPoint(feature.centroid))
                                        .putRemovable(BOUNDARY, formatGeometry(feature.boundary))
                                        .putRemovable(TILES, formatTiles(feature.tiles))
                                }
                            )
                    )
            )
            .get()
    }

    private fun error(response: ErrorGeoResponse): JsonObject {
        return FluentJsonObject()
            .put(STATUS, ResponseStatus.ERROR)
            .put(MESSAGE, response.message)
            .get()
    }

    private fun ambiguous(response: AmbiguousGeoResponse): JsonObject {
        return FluentJsonObject()
            .put(STATUS, ResponseStatus.AMBIGUOUS)
            .put(MESSAGE, "Ambiguous")
            .put(DATA, FluentJsonObject()
                .put(LEVEL, response.featureLevel)
                .put(FEATURES, FluentJsonArray()
                    .addAll(response.features.map { feature ->
                        FluentJsonObject()
                            .put(QUERY, feature.request)
                            .put(NAMESAKE_COUNT, feature.namesakeCount)
                            .put(NAMESAKE_EXAMPLES, FluentJsonArray()
                                .addAll(feature.namesakes.map { namesake ->
                                    FluentJsonObject()
                                        .put(NAMESAKE_NAME, namesake.name)
                                        .put(NAMESAKE_PARENTS, FluentJsonArray()
                                            .addAll(namesake.parents.map { parent ->
                                                FluentJsonObject()
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


    private fun stringArray(v: List<String>?): JsonArray? {
        return if (v == null || v.isEmpty()) null else JsonArray(v)
    }

    private fun formatRect(v: GeoRectangle?): JsonObject? {
        return if (v == null) {
            null
        } else ProtocolJsonHelper.formatGeoRectangle(v)

    }

    private fun formatPoint(v: DoubleVector?): FluentJsonValue? {
        return v?.let { FluentJsonObject()
            .put(LON, it.x)
            .put(LAT, it.y)
        }
    }

    private fun formatGeometry(geometry: Geometry?): String? {
        return geometry?.let { geometryToString(it) }
    }

    private fun geometryToString(geometry: Geometry): String {
        return StringGeometries.getRawData(geometry)
    }

    private fun formatTiles(tiles: List<GeoTile>?): FluentJsonObject? {
        return tiles?.let {
            val obj = FluentJsonObject()
            for (tile in tiles) {
                val geometries = FluentJsonArray()

                for (boundary in tile.geometries) {
                    geometries.add(geometryToString(boundary))
                }

                obj.put(tile.key.string, geometries)
            }

            return obj
        }
    }

}
