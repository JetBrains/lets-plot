/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.geoprotocol.*
import jetbrains.gis.geoprotocol.json.RequestAssertions.assertThatExplicitRequest
import jetbrains.gis.geoprotocol.json.RequestAssertions.assertThatGeocodingRequest
import jetbrains.gis.geoprotocol.json.RequestAssertions.assertThatReverseRequest
import jetbrains.gis.geoprotocol.json.RequestJsonFormatter.format
import kotlin.test.Test

class RequestJsonTest {
    private val PARENT_REGION = MapRegion.withName("foo")
    private val CLOSEST_COORD = DoubleVector(1.0, 2.0)
    private val FEATURE_LEVEL = FeatureLevel.STATE
    private val IDS = listOf("1", "2", "3")
    private val REVERSE_GEOCODING_COORDINATE = DoubleVector(1.0, 2.0)
    private val REVERSE_GEOCODING_COORDINATES = listOf(REVERSE_GEOCODING_COORDINATE)
    private val AMBIGUITY_RESOLVING_BOX = DoubleRectangle(0.0, 1.0, 2.0, 3.0)

    @Test
    fun explicitWithAllParameters() {

        val tiles = mapOf(
            "asd" to listOf<QuadKey<LonLat>>(
                QuadKey("032"),
                QuadKey("033"),
                QuadKey("210"),
                QuadKey("211")
            )
        )

        val json = format(
            GeoRequestBuilder.ExplicitRequestBuilder()
                .setIds(IDS)
                .setResolution(13)
                .setTiles(tiles)
                .addFeature(GeoRequest.FeatureOption.CENTROID)
                .addFeature(GeoRequest.FeatureOption.POSITION)
                .build()
        )

        val request = RequestJsonParser.parse(json) as GeoRequest.ExplicitSearchRequest

        assertThatExplicitRequest(request)
            .hasIds(IDS)
            .hasLevelOfDetails(LevelOfDetails.CITY_LOW)
            .hasTiles(tiles)
            .hasFeatures(GeoRequest.FeatureOption.CENTROID, GeoRequest.FeatureOption.POSITION)
    }

    @Test
    fun explicitWithEmptyParameters() {

        val json = format(
            GeoRequestBuilder.ExplicitRequestBuilder()
                .setIds(IDS)
                .build()
        )

        val request = RequestJsonParser.parse(json) as GeoRequest.ExplicitSearchRequest

        assertThatExplicitRequest(request)
            .hasIds(IDS)
            .hasNoLevelOfDetails()
            .hasNoTiles()
            .hasNoFeatures()
    }

    @Test
    fun geocodingAllParameters() {
        val query = GeoRequestBuilder.RegionQueryBuilder()
            .setQueryNames("foo")
            .setParent(PARENT_REGION)
            .setAmbiguityResolver(GeoRequest.GeocodingSearchRequest.AmbiguityResolver.closestTo(CLOSEST_COORD))
            .build()

        val json = format(
            GeoRequestBuilder.GeocodingRequestBuilder()
                .addQuery(query)
                .setLevel(FEATURE_LEVEL)
                .setNamesakeExampleLimit(12)
                .build()
        )

        val request = RequestJsonParser.parse(json) as GeoRequest.GeocodingSearchRequest

        assertThatGeocodingRequest(request)
            .hasLevel(FEATURE_LEVEL)
            .hasNamesakeExampleLimit(12)
            .hasQueries(query)
    }

    @Test
    fun reverseWithoutParent() {

        val json = format(
            GeoRequestBuilder.ReverseGeocodingRequestBuilder()
                .setCoordinates(REVERSE_GEOCODING_COORDINATES)
                .setLevel(FEATURE_LEVEL)
                .build()
        )

        val request = RequestJsonParser.parse(json) as GeoRequest.ReverseGeocodingSearchRequest

        assertThatReverseRequest(request)
            .hasCoordinates(REVERSE_GEOCODING_COORDINATE)
            .hasLevel(FEATURE_LEVEL)
            .hasNoParent()
    }

    @Test
    fun reverseWithParent() {

        val json = format(
            GeoRequestBuilder.ReverseGeocodingRequestBuilder()
                .setCoordinates(REVERSE_GEOCODING_COORDINATES)
                .setLevel(FEATURE_LEVEL)
                .setParent(PARENT_REGION)
                .build()
        )

        val request = RequestJsonParser.parse(json) as GeoRequest.ReverseGeocodingSearchRequest

        assertThatReverseRequest(request)
            .hasCoordinates(REVERSE_GEOCODING_COORDINATE)
            .hasLevel(FEATURE_LEVEL)
            .hasParent(MapRegion.withName("foo"))
    }


    @Test
    fun ambiguityResolvingWithBox() {
        val queryWithBox = GeoRequestBuilder.RegionQueryBuilder()
            .setQueryNames("foo")
            .setAmbiguityResolver(GeoRequest.GeocodingSearchRequest.AmbiguityResolver.within(AMBIGUITY_RESOLVING_BOX))
            .build()

        val json = format(
            GeoRequestBuilder.GeocodingRequestBuilder()
                .addQuery(queryWithBox)
                .setLevel(FEATURE_LEVEL)
                .build()
        )

        val request = RequestJsonParser.parse(json) as GeoRequest.GeocodingSearchRequest

        assertThatGeocodingRequest(request)
            .hasQueries(queryWithBox)
            .hasLevel(FEATURE_LEVEL)
    }
}