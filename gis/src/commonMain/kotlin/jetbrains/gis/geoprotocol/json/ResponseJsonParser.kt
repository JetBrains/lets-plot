package jetbrains.gis.protocol.json

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.json.JsonArray
import jetbrains.datalore.base.json.JsonValue
import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.gis.common.json.FluentJsonObject
import jetbrains.gis.common.json.JsonUtils.stringStreamOf
import jetbrains.gis.protocol.FeatureLevel
import jetbrains.gis.protocol.GeoResponse
import jetbrains.gis.protocol.GeoResponse.ErrorGeoResponse
import jetbrains.gis.protocol.GeoResponseBuilder.*
import jetbrains.gis.protocol.GeoTile
import jetbrains.gis.protocol.Geometry
import java.util.stream.Collectors.toList

object ResponseJsonParser {
    fun parse(json: JsonValue): GeoResponse {
        val responseJson = FluentJsonObject(json)
        val status = responseJson.getEnum(STATUS, ResponseStatus.values())

        when (status) {
            ResponseJsonParser.ResponseStatus.SUCCESS -> return success(responseJson)

            ResponseJsonParser.ResponseStatus.AMBIGUOUS -> return ambiguous(responseJson)

            ResponseJsonParser.ResponseStatus.ERROR -> return error(responseJson)

            else -> throw IllegalStateException("Unknown response status: $status")
        }
    }

    private fun success(responseJson: FluentJsonObject): GeoResponse {
        val successResponse = SuccessResponseBuilder()
        responseJson
            .getObject(DATA, { data ->
                data
                    .getOptionalEnum(LEVEL, ???({ successResponse.setLevel() }), FeatureLevel.values())
                .forObjects(FEATURES) { featureJson ->
                val feature = GeocodedFeatureBuilder()
                featureJson
                    .getString(QUERY, ???({ feature.setQuery() }))
                .getString(ID, ???({ feature.setId() }))
                .getString(NAME, ???({ feature.setName() }))
                .forExistingStrings(HIGHLIGHTS, ???({ feature.addHighlight() }))
                .getExistingString(BOUNDARY) { boundaryGeoJson -> feature.setBoundary(readGeometry(boundaryGeoJson)) }
                .getExistingObject(TILES, { tileJson ->
                    tileJson.forEntries { quadKey, geometry ->
                        feature.addTile(
                            GeoTile(
                                QuadKey(quadKey),
                                stringStreamOf(geometry as JsonArray).map(??? { readTile(it) }).collect(toList<T>())
                        )
                        )
                    }
                }
                )
                .getExistingObject(CENTROID, { centroidJson -> feature.setCentroid(parseCentroid(centroidJson)) })
                .getExistingObject(LIMIT, { limitJson -> feature.setLimit(parseGeoRectangle(limitJson)) })
                .getExistingObject(POSITION, { positionJson -> feature.setPosition(parseGeoRectangle(positionJson)) })
                successResponse.addGeocodedFeature(feature.build())
            }
            }
            )
        return successResponse.build()
    }

    private fun ambiguous(responseJson: FluentJsonObject): GeoResponse {
        val ambiguousResponse = AmbiguousResponseBuilder()
        responseJson
            .getObject(DATA, { data ->
                data
                    .getOptionalEnum(LEVEL, ???({ ambiguousResponse.setLevel() }), FeatureLevel.values())
                .forObjects(FEATURES) { featureJson ->
                val feature = AmbiguousFeatureBuilder()
                featureJson
                    .getString(QUERY, ???({ feature.setQuery() }))
                .getInt(NAMESAKE_COUNT, ???({ feature.setTotalNamesakeCount() }))
                .forObjects(NAMESAKE_EXAMPLES) { namesakeJson ->
                val namesake = NamesakeBuilder()

                namesakeJson
                    .getString(NAMESAKE_NAME, ???({ namesake.setName() }))
                .forObjects(NAMESAKE_PARENTS) { parentJson ->
                parentJson
                    .getString(NAMESAKE_NAME, ???({ namesake.addParentName() }))
                .getEnum(LEVEL, ???({ namesake.addParentLevel() }), FeatureLevel.values())
            }

                feature.addNamesakeExample(namesake.build())
            }
                ambiguousResponse.addAmbiguousFeature(feature.build())
            }
            })
        return ambiguousResponse.build()
    }

    private fun error(responseJson: FluentJsonObject): GeoResponse {
        return ErrorGeoResponse(responseJson.getString(MESSAGE))
    }

    private fun parseCentroid(centroid: FluentJsonObject): DoubleVector {
        return DoubleVector(
            centroid.getDouble(LON),
            centroid.getDouble(LAT)
        )
    }

    private fun readGeometry(geoJson: String): Geometry {
        return StringGeometries.fromGeoJson(geoJson)
    }

    private fun readTile(geometry: String): Geometry {
        return StringGeometries.fromTwkb(geometry)
    }

    private fun parseGeoRectangle(data: FluentJsonObject): GeoRectangle {
        return ProtocolJsonHelper.parseGeoRectangle(data.get())
    }

    enum class ResponseStatus {
        SUCCESS,
        AMBIGUOUS,
        ERROR
    }

}
