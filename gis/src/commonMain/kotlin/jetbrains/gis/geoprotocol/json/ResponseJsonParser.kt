package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.gis.common.json.FluentJsonObject
import jetbrains.gis.common.json.JsonArray
import jetbrains.gis.common.json.JsonObject
import jetbrains.gis.common.json.JsonUtils.stringStreamOf
import jetbrains.gis.geoprotocol.*
import jetbrains.gis.geoprotocol.GeoResponse.ErrorGeoResponse
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

object ResponseJsonParser {
    fun parse(json: JsonObject): GeoResponse {
        val responseJson = FluentJsonObject(json)

        return when (val status = responseJson.getEnum(STATUS, ResponseStatus.values())) {
            ResponseStatus.SUCCESS -> success(responseJson)
            ResponseStatus.AMBIGUOUS -> ambiguous(responseJson)
            ResponseStatus.ERROR -> error(responseJson)
            else -> throw IllegalStateException("Unknown response status: $status")
        }
    }

    private fun success(responseJson: FluentJsonObject): GeoResponse {
        val successResponse = GeoResponseBuilder.SuccessResponseBuilder()
        responseJson
            .getObject(DATA) { data ->
                data
                    .getOptionalEnum(LEVEL, successResponse::setLevel, FeatureLevel.values())
                .forObjects(FEATURES) { featureJson ->
                    val feature = GeoResponseBuilder.GeocodedFeatureBuilder()
                    featureJson
                        .getString(QUERY, feature::setQuery)
                        .getString(ID, feature::setId)
                        .getString(NAME, feature::setName)
                        .forExistingStrings(HIGHLIGHTS, feature::addHighlight)
                        .getExistingString(BOUNDARY) { feature.setBoundary(readGeometry(it)) }
                        .getExistingObject(CENTROID) { feature.setCentroid(parseCentroid(it)) }
                        .getExistingObject(LIMIT) { feature.setLimit(parseGeoRectangle(it)) }
                        .getExistingObject(POSITION) { feature.setPosition(parseGeoRectangle(it)) }
                        .getExistingObject(TILES) { tiles ->
                            tiles
                                .forEntries { quadKey, geometry ->
                                    feature
                                        .addTile(
                                            GeoTile(
                                                QuadKey(quadKey),
                                                stringStreamOf(geometry as JsonArray).map { s -> readTile(s!!) }.toList()
                                            )
                                        )
                                }
                        }
                    successResponse.addGeocodedFeature(feature.build())
                }
            }
        return successResponse.build()
    }

    private fun ambiguous(responseJson: FluentJsonObject): GeoResponse {
        val ambiguousResponse = GeoResponseBuilder.AmbiguousResponseBuilder()
        responseJson
            .getObject(DATA) { data ->
                data
                    .getOptionalEnum(LEVEL, ambiguousResponse::setLevel, FeatureLevel.values())
                .forObjects(FEATURES) { featureJson ->
                    val feature = GeoResponseBuilder.AmbiguousFeatureBuilder()
                    featureJson
                        .getString(QUERY, feature::setQuery)
                        .getInt(NAMESAKE_COUNT, feature::setTotalNamesakeCount)
                        .forObjects(NAMESAKE_EXAMPLES) { namesakeJson ->
                            val namesake = GeoResponseBuilder.NamesakeBuilder()

                            namesakeJson
                                .getString(NAMESAKE_NAME, namesake::setName)
                                .forObjects(NAMESAKE_PARENTS) { parentJson ->
                                    parentJson
                                        .getString(NAMESAKE_NAME, namesake::addParentName)
                                        .getEnum(LEVEL, namesake::addParentLevel, FeatureLevel.values())
                                }

                            feature.addNamesakeExample(namesake.build())
                        }
                    ambiguousResponse.addAmbiguousFeature(feature.build())
                }
            }
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
}
