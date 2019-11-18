/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.common.json.FluentObject
import jetbrains.gis.common.json.Obj
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
    fun parse(json: Obj): GeoResponse {
        val responseJson = FluentObject(json)

        return when (val status = responseJson.getEnum(STATUS, ResponseStatus.values())) {
            ResponseStatus.SUCCESS -> success(responseJson)
            ResponseStatus.AMBIGUOUS -> ambiguous(responseJson)
            ResponseStatus.ERROR -> error(responseJson)
            else -> throw IllegalStateException("Unknown response status: $status")
        }
    }

    private fun success(responseJson: FluentObject): GeoResponse {
        val successResponse = GeoResponseBuilder.SuccessResponseBuilder()
        responseJson
            .getObject(DATA) { data -> data
                .getOptionalEnum(LEVEL, { successResponse.setLevel(it) }, FeatureLevel.values())
                .forObjects(FEATURES) { featureJson ->
                    val feature = GeoResponseBuilder.GeocodedFeatureBuilder()
                    featureJson
                        .getString(QUERY) { feature.setQuery(it) }
                        .getString(ID) { feature.setId(it) }
                        .getString(NAME) { feature.setName(it) }
                        .forExistingStrings(HIGHLIGHTS) { feature.addHighlight(it) }
                        .getExistingString(BOUNDARY) { feature.setBoundary(readGeometry(it)) }
                        .getExistingObject(CENTROID) { feature.setCentroid(parseCentroid(it)) }
                        .getExistingObject(LIMIT) { feature.setLimit(parseGeoRectangle(it)) }
                        .getExistingObject(POSITION) { feature.setPosition(parseGeoRectangle(it)) }
                        .getExistingObject(TILES) { tiles -> tiles
                            .forArrEntries() { quadKey, geometry -> feature
                                .addTile(GeoTile(
                                    QuadKey(quadKey),
                                    geometry.map { readTile(it!! as String) }
                                ))
                            }
                        }
                    successResponse.addGeocodedFeature(feature.build())
                }
            }
        return successResponse.build()
    }

    private fun ambiguous(responseJson: FluentObject): GeoResponse {
        val ambiguousResponse = GeoResponseBuilder.AmbiguousResponseBuilder()
        responseJson
            .getObject(DATA) { data -> data
                .getOptionalEnum(LEVEL, { ambiguousResponse.setLevel(it) }, FeatureLevel.values())
                .forObjects(FEATURES) { featureJson ->
                    val feature = GeoResponseBuilder.AmbiguousFeatureBuilder()
                    featureJson
                        .getString(QUERY) { feature.setQuery(it) }
                        .getInt(NAMESAKE_COUNT) { feature.setTotalNamesakeCount(it) }
                        .forObjects(NAMESAKE_EXAMPLES) { namesakeJson ->
                            val namesake = GeoResponseBuilder.NamesakeBuilder()

                            namesakeJson
                                .getString(NAMESAKE_NAME) { namesake.setName(it) }
                                .forObjects(NAMESAKE_PARENTS) { parentJson -> parentJson
                                    .getString(NAMESAKE_NAME) { namesake.addParentName(it) }
                                    .getEnum(LEVEL, { namesake.addParentLevel(it) }, FeatureLevel.values())
                                }

                            feature.addNamesakeExample(namesake.build())
                        }
                    ambiguousResponse.addAmbiguousFeature(feature.build())
                }
            }
        return ambiguousResponse.build()
    }

    private fun error(responseJson: FluentObject): GeoResponse {
        return ErrorGeoResponse(responseJson.getString(MESSAGE))
    }

    private fun parseCentroid(centroid: FluentObject): Vec<Generic> {
        return explicitVec<Generic>(
            centroid.getDouble(LON),
            centroid.getDouble(LAT)
        )
    }

    private fun readGeometry(geoJson: String): Boundary<Generic> {
        return StringGeometries.fromGeoJson(geoJson)
    }

    private fun readTile(geometry: String): Boundary<Generic> {
        return StringGeometries.fromTwkb(geometry)
    }

    private fun parseGeoRectangle(data: FluentObject): GeoRectangle {
        return ProtocolJsonHelper.parseGeoRectangle(data.get())
    }
}
