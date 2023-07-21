/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.geoprotocol.json


import org.jetbrains.letsPlot.commons.intern.spatial.GeoRectangle
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Untyped
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import org.jetbrains.letsPlot.gis.geoprotocol.Boundaries
import org.jetbrains.letsPlot.gis.geoprotocol.FeatureLevel
import org.jetbrains.letsPlot.gis.geoprotocol.FeatureLevel.STATE
import org.jetbrains.letsPlot.gis.geoprotocol.GeoResponse.AmbiguousGeoResponse
import org.jetbrains.letsPlot.gis.geoprotocol.GeoResponse.SuccessGeoResponse
import org.jetbrains.letsPlot.gis.geoprotocol.GeoResponseBuilder
import org.jetbrains.letsPlot.gis.geoprotocol.json.ResponseAssertions.assertThatAmbiguousResponse
import org.jetbrains.letsPlot.gis.geoprotocol.json.ResponseAssertions.assertThatSuccessResponse
import kotlin.test.Test
/*
class GeoResponseJsonTest {

    @Test
    fun successResponse() {
        val json = ResponseJsonFormatter.format(
            GeoResponseBuilder.SuccessResponseBuilder()
                .setLevel(STATE)
                .addGeocodedFeature(GEOCODED_FEATURE_WITH_DATA)
                .addGeocodedFeature(GEOCODED_FEATURE_WITHOUT_DATA)
                .build()
        )

        val response = ResponseJsonParser.parse(json) as SuccessGeoResponse

        assertThatSuccessResponse(response)
            .hasFeatureLevel(STATE)
            .hasFeatures(GEOCODED_FEATURE_WITH_DATA, GEOCODED_FEATURE_WITHOUT_DATA)
    }

    @Test
    fun ambiguousResponse() {
        val json = ResponseJsonFormatter.format(
            GeoResponseBuilder.AmbiguousResponseBuilder()
                .setLevel(STATE)
                .addAmbiguousFeature(AMBIGUOUS_FEATURE_WITH_NAMESAKES)
                .addAmbiguousFeature(AMBIGUOUS_FEATURE_WITHOUT_NAMESAKES)
                .addAmbiguousFeature(NOT_FOUND_FEATURE)
                .build()
        )

        val response = ResponseJsonParser.parse(json) as AmbiguousGeoResponse

        assertThatAmbiguousResponse(response)
            .hasFeatureLevel(STATE)
            .hasAmbiguousFeatures(
                AMBIGUOUS_FEATURE_WITH_NAMESAKES,
                AMBIGUOUS_FEATURE_WITHOUT_NAMESAKES,
                NOT_FOUND_FEATURE
            )
    }

    companion object {
        private val GEOMETRY = Boundaries.fromGeoJson("Raw geometry string")

        private val GEOCODED_FEATURE_WITH_DATA = GeoResponseBuilder.GeocodedFeatureBuilder()
            .setQuery("TX")
            .setId("42")
            .setName("Texas")
            .addHighlight("TX")
            .addHighlight("Texas")
            .setCentroid(point(12.3, 34.5))
            .setPosition(rectangle(-34.5, -12.3, 27.0, 16.1))
            .setLimit(rectangle(-54.3, -20.8, 27.0, 18.9))
            .setBoundary(GEOMETRY)
            .build()

        private val GEOCODED_FEATURE_WITHOUT_DATA = GeoResponseBuilder.GeocodedFeatureBuilder()
            .setQuery("CA")
            .setId("18")
            .setName("California")
            .build()

        private val AMBIGUOUS_FEATURE_WITH_NAMESAKES = GeoResponseBuilder.AmbiguousFeatureBuilder()
            .setQuery("Dakota")
            .setTotalNamesakeCount(2)
            .addNamesakeExample(
                GeoResponseBuilder.NamesakeBuilder()
                    .setName("South Dakota")
                    .addParent("USA", FeatureLevel.COUNTRY)
                    .build()
            )
            .addNamesakeExample(
                GeoResponseBuilder.NamesakeBuilder()
                    .setName("North Dakota")
                    .addParent("USA", FeatureLevel.COUNTRY)
                    .build()
            )
            .build()

        private val AMBIGUOUS_FEATURE_WITHOUT_NAMESAKES = GeoResponseBuilder.AmbiguousFeatureBuilder()
            .setQuery("York")
            .setTotalNamesakeCount(7)
            .build()

        private val NOT_FOUND_FEATURE = GeoResponseBuilder.AmbiguousFeatureBuilder()
            .setQuery("NoName")
            .setTotalNamesakeCount(0)
            .build()

        private fun point(lon: Double, lat: Double): Vec<Generic> {
            return explicitVec(lon, lat)
        }

        private fun rectangle(minLon: Double, minLat: Double, maxLon: Double, maxLat: Double): GeoRectangle {
            return GeoRectangle(minLon, minLat, maxLon, maxLat)
        }
    }
}

private fun GeoResponseBuilder.NamesakeBuilder.addParent(name: String, level: FeatureLevel)
        = apply { addParentLevel(level).addParentName(name) }
*/