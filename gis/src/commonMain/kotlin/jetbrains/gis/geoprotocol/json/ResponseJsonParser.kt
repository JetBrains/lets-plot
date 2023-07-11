/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol.json

import org.jetbrains.letsPlot.commons.intern.json.FluentObject
import org.jetbrains.letsPlot.commons.intern.json.Obj
import org.jetbrains.letsPlot.commons.intern.spatial.GeoRectangle
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Untyped
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import jetbrains.gis.geoprotocol.*
import jetbrains.gis.geoprotocol.GeoResponse.ErrorGeoResponse
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeoParent
import jetbrains.gis.geoprotocol.json.ResponseKeys.ANSWERS
import jetbrains.gis.geoprotocol.json.ResponseKeys.BOUNDARY
import jetbrains.gis.geoprotocol.json.ResponseKeys.CENTROID
import jetbrains.gis.geoprotocol.json.ResponseKeys.DATA
import jetbrains.gis.geoprotocol.json.ResponseKeys.FEATURES
import jetbrains.gis.geoprotocol.json.ResponseKeys.FRAGMENTS
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
import jetbrains.gis.geoprotocol.json.ResponseKeys.PARENTS
import jetbrains.gis.geoprotocol.json.ResponseKeys.PARENT_ID
import jetbrains.gis.geoprotocol.json.ResponseKeys.PARENT_LEVEL
import jetbrains.gis.geoprotocol.json.ResponseKeys.PARENT_NAME
import jetbrains.gis.geoprotocol.json.ResponseKeys.POSITION
import jetbrains.gis.geoprotocol.json.ResponseKeys.QUERY
import jetbrains.gis.geoprotocol.json.ResponseKeys.STATUS

object ResponseJsonParser {
    fun parse(json: Obj): GeoResponse {
        val responseJson = FluentObject(json)

        return when (val status: ResponseStatus = responseJson.getEnum(STATUS, ResponseStatus.values())) {
            ResponseStatus.SUCCESS -> success(responseJson)
            ResponseStatus.AMBIGUOUS -> ambiguous(responseJson)
            ResponseStatus.ERROR -> error(responseJson)
//            else -> throw IllegalStateException("Unknown response status: $status")
        }
    }

    private fun success(responseJson: FluentObject): GeoResponse {
        val successResponse = GeoResponseBuilder.SuccessResponseBuilder()
        responseJson
            .getObject(DATA) { data ->
                data
                    .getOptionalEnum(LEVEL, successResponse::setLevel, FeatureLevel.values())
                    .forObjects(ANSWERS) { answerJson ->
                        val answer = GeoResponseBuilder.GeocodingAnswerBuilder()
                        answerJson
                            .forObjects(FEATURES) { featureJson ->
                                val feature = GeoResponseBuilder.GeocodedFeatureBuilder()
                                featureJson
                                    .getString(ID, feature::setId)
                                    .getString(NAME, feature::setName)
                                    .forExistingStrings(HIGHLIGHTS, feature::addHighlight)
                                    .getExistingString(BOUNDARY) { feature.setBoundary(readGeometry(it)) }
                                    .forObjects(PARENTS) { feature.addParent(parseGeoParent(it)) }
                                    .getExistingObject(CENTROID) { feature.setCentroid(parseCentroid(it)) }
                                    .getExistingObject(LIMIT) { feature.setLimit(parseGeoRectangle(it)) }
                                    .getExistingObject(POSITION) { feature.setPosition(parseGeoRectangle(it)) }
                                    .getExistingObject(FRAGMENTS) { fragments ->
                                        fragments
                                            .forArrEntries() { quadKey, boundary ->
                                                feature
                                                    .addFragment(Fragment(
                                                        QuadKey(quadKey),
                                                        boundary.map { readBoundary(it!! as String) }
                                                    ))
                                            }
                                    }
                                answer.addGeocodedFeature(feature.build())
                            }
                        successResponse.addGeocodingAnswer(answer.build())
                    }
            }
        return successResponse.build()
    }

    private fun ambiguous(responseJson: FluentObject): GeoResponse {
        val ambiguousResponse = GeoResponseBuilder.AmbiguousResponseBuilder()
        responseJson
            .getObject(DATA) { data ->
                data
                    .getOptionalEnum(LEVEL, { ambiguousResponse.setLevel(it) }, FeatureLevel.values())
                    .forObjects(FEATURES) { featureJson ->
                        val feature = GeoResponseBuilder.AmbiguousFeatureBuilder()
                        featureJson
                            .getString(QUERY) { feature.setQuery(it) }
                            .getInt(NAMESAKE_COUNT) { feature.setTotalNamesakeCount(it) }
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

    private fun error(responseJson: FluentObject): GeoResponse {
        return ErrorGeoResponse(responseJson.getString(MESSAGE))
    }

    private fun parseCentroid(centroid: FluentObject): Vec<Untyped> {
        return explicitVec<Untyped>(
            centroid.getDouble(LON),
            centroid.getDouble(LAT)
        )
    }

    private fun readGeometry(geoJson: String): Boundary<Untyped> {
        return Boundaries.fromGeoJson(geoJson)
    }

    private fun readBoundary(boundary: String): Boundary<Untyped> {
        return Boundaries.fromTwkb(boundary)
    }

    private fun parseGeoRectangle(data: FluentObject): GeoRectangle {
        return ProtocolJsonHelper.parseGeoRectangle(data.get())
    }

    private fun parseGeoParent(parent: FluentObject): GeoParent {
        return GeoParent(
            parent.getString(PARENT_ID),
            parent.getString(PARENT_NAME),
            parent.getEnum(PARENT_LEVEL, FeatureLevel.values())
        )
    }
}
