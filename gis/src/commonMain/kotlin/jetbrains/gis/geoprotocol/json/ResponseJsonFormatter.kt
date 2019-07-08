package jetbrains.gis.protocol.json

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.json.JsonArray
import jetbrains.datalore.base.json.JsonObject
import jetbrains.datalore.base.json.JsonString
import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.gis.common.json.FluentJsonArray
import jetbrains.gis.common.json.FluentJsonObject
import jetbrains.gis.common.json.FluentJsonValue
import jetbrains.gis.protocol.GeoResponse
import jetbrains.gis.protocol.GeoResponse.AmbiguousGeoResponse
import jetbrains.gis.protocol.GeoResponse.ErrorGeoResponse
import jetbrains.gis.protocol.GeoResponse.SuccessGeoResponse
import jetbrains.gis.protocol.GeoTile
import jetbrains.gis.protocol.Geometry
import java.util.stream.Collectors.toList

object ResponseJsonFormatter {
    fun format(response: GeoResponse): JsonObject {
        if (response is SuccessGeoResponse) {
            return ResponseJsonFormatter.success(response as SuccessGeoResponse)
        }

        if (response is AmbiguousGeoResponse) {
            return ResponseJsonFormatter.ambiguous(response as AmbiguousGeoResponse)
        }

        return if (response is ErrorGeoResponse) {
            ResponseJsonFormatter.error(response as ErrorGeoResponse)
        } else ResponseJsonFormatter.error(ErrorGeoResponse("Unknown response: " + response.getClass().getName()))

    }

    private fun success(response: SuccessGeoResponse): JsonObject {
        return FluentJsonObject()
            .put(STATUS, ResponseStatus.SUCCESS)
            .put(MESSAGE, "OK")
            .put(
                DATA, FluentJsonObject()
                    .put(LEVEL, response.getFeatureLevel())
                    .put(
                        FEATURES, FluentJsonArray()
                            .addAll(
                                response.getFeatures().stream().map(
                                    { feature ->
                                        FluentJsonObject()
                                            .put(QUERY, feature.getRequest())
                                            .put(ID, feature.getID())
                                            .put(NAME, feature.getName())
                                            .putRemovable(HIGHLIGHTS, stringArray(feature.getHighlights()))
                                            .putRemovable(LIMIT, formatRect(feature.getLimit()))
                                            .putRemovable(POSITION, formatRect(feature.getPosition()))
                                            .putRemovable(CENTROID, formatPoint(feature.getCentroid()))
                                            .putRemovable(BOUNDARY, formatGeometry(feature.getBoundary()))
                                            .putRemovable(TILES, formatTiles(feature.getTiles()))
                                    })
                                    .collect(toList<T>())
                            )
                    )
            )
            .get()
    }

    private fun error(response: ErrorGeoResponse): JsonObject {
        return FluentJsonObject()
            .put(STATUS, ResponseStatus.ERROR)
            .put(MESSAGE, response.getMessage())
            .get()
    }

    private fun ambiguous(response: AmbiguousGeoResponse): JsonObject {
        return FluentJsonObject()
            .put(STATUS, ResponseStatus.AMBIGUOUS)
            .put(MESSAGE, "Ambiguous")
            .put(DATA, FluentJsonObject()
                .put(LEVEL, response.getFeatureLevel())
                .put(FEATURES, FluentJsonArray()
                    .addAll(response.getFeatures().stream().map(
                        { feature ->
                            FluentJsonObject()
                                .put(QUERY, feature.getRequest())
                                .put(NAMESAKE_COUNT, feature.getNamesakeCount())
                                .put(NAMESAKE_EXAMPLES, FluentJsonArray()
                                    .addAll(feature.getNamesakes().stream().map(
                                        { namesake ->
                                            FluentJsonObject()
                                                .put(NAMESAKE_NAME, namesake.getName())
                                                .put(NAMESAKE_PARENTS, FluentJsonArray()
                                                    .addAll(namesake.getParents().stream().map(
                                                        { parent ->
                                                            FluentJsonObject()
                                                                .put(
                                                                    NAMESAKE_NAME,
                                                                    parent.getName()
                                                                )
                                                                .put(
                                                                    LEVEL,
                                                                    parent.getLevel()
                                                                )
                                                        }
                                                    ).collect(toList<T>())
                                                    )
                                                )
                                        }
                                    ).collect(toList<T>())
                                    )
                                )
                        }
                    ).collect(toList<T>())
                    )
                )
            ).get()
    }


    private fun stringArray(v: List<String>?): JsonArray? {
        if (v == null || v.isEmpty()) {
            return null
        }

        val jsonArray = JsonArray()
        v.forEach(Consumer<String> { jsonArray.add() })
        return jsonArray
    }

    private fun formatRect(v: GeoRectangle?): JsonObject? {
        return if (v == null) {
            null
        } else ProtocolJsonHelper.formatGeoRectangle(v)

    }

    private fun formatPoint(v: DoubleVector?): FluentJsonValue? {
        return if (v == null) {
            null
        } else FluentJsonObject()
            .put(LON, v!!.x)
            .put(LAT, v!!.y)

    }

    private fun formatGeometry(geometry: Geometry?): JsonString? {
        return if (geometry == null) {
            null
        } else geometryToString(geometry)
    }

    private fun geometryToString(geometry: Geometry): JsonString {
        return JsonString(StringGeometries.getRawData(geometry))
    }

    private fun formatTiles(tiles: List<GeoTile>): FluentJsonObject {
        val obj = FluentJsonObject()
        for (tile in tiles) {
            val geometries = FluentJsonArray()

            for (boundary in tile.getGeometries()) {
                geometries.add(geometryToString(boundary))
            }

            obj.put(tile.getKey().getString(), geometries)
        }

        return obj
    }

}
