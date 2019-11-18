/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.common.json.Obj
import jetbrains.gis.geoprotocol.GeoRequest.*
import jetbrains.gis.geoprotocol.GeoRequest.GeocodingSearchRequest.AmbiguityResolver.IgnoringStrategy
import jetbrains.gis.geoprotocol.GeoRequestBuilder.GeocodingRequestBuilder
import jetbrains.gis.geoprotocol.GeoRequestBuilder.RegionQueryBuilder
import jetbrains.gis.geoprotocol.LevelOfDetails
import jetbrains.gis.geoprotocol.MapRegion
import kotlin.test.Test


class JsonRequestParsingTest {

    @Test
    fun simpleRequestWithId() {
        val request = parseExplicitRequest(
            RequestJsonBuilder.explicit("73979", "122641")
                .featureOptions(FeatureOption.POSITION, FeatureOption.CENTROID)
                .build()
        )

        RequestAssertions.ExplicitSearchRequestAssertion(request)
            .hasIds(listOf("73979", "122641"))
            .hasFeatures(FeatureOption.POSITION, FeatureOption.CENTROID)
            .hasNoTiles()
            .hasNoLevelOfDetails();
    }


    @Test
    fun whenResolutionAndBoundaryAreSet() {
        val request = parseGeocodingRequest(
            RequestJsonBuilder.geocoding()
                .resolution(RESOLUTION)
                .tiles(TILE_IDS)
                .build()
        )

        RequestAssertions.GeocodingSearchRequestAssertion(request)
            .hasLevelOfDetails(LevelOfDetails.fromResolution(RESOLUTION))
            .hasTiles(TILE_IDS);
    }

    @Test
    fun whenResolutionIsEmpty() {
        val request = parseGeocodingRequest(
            RequestJsonBuilder.geocoding()
                .resolution(null)
                .tiles(null)
                .build()
        )

        RequestAssertions.GeocodingSearchRequestAssertion(request)
            .hasNoLevelOfDetails()
            .hasNoTiles();
    }

    @Test
    fun byNameWithoutLevelRequest() {
        RequestJsonParser.parse(
            RequestJsonBuilder.geocoding()
                .autoDetectLevel()
                .build()
        )
    }

    @Test
    fun withoutFeaturesAsRegionObject() {
        parseGeocodingRequest(
            RequestJsonBuilder.geocoding("texas").build()
        )
    }

    @Test
    fun countriesRequest() {
        parseGeocodingRequest(
            RequestJsonBuilder.geocoding().country().build()
        )
    }

    @Test
    fun withFeatures() {
        parseGeocodingRequest(
            RequestJsonBuilder.geocoding().position().build()
        )
    }

    @Test
    fun withHighlights() {
        parseGeocodingRequest(
            RequestJsonBuilder.geocoding().highlights().build()
        )
    }

    @Test
    fun byIdWithoutLevel() {
        parseExplicitRequest(
            RequestJsonBuilder.explicit("123").position().build()
        )
    }

    @Test
    fun withParentById() {
        val request = parseGeocodingRequest(
            RequestJsonBuilder.geocoding("NY").state().regionId("123", "456").build()
        )

        RequestAssertions.GeocodingSearchRequestAssertion(request)
            .hasQueries(
                RegionQueryBuilder()
                    .setQueryNames("NY")
                    .setParent(MapRegion.withIdList(listOf("123", "456")))
                    .build()
            )
    }

    @Test
    fun ambiguityReactionTest() {
        val query = RegionQueryBuilder()
            .setQueryNames(listOf("foo"))
            .setIgnoringStrategy(IgnoringStrategy.SKIP_MISSING)
            .build()

        val json = RequestJsonFormatter.format(
            GeocodingRequestBuilder()
                .addQuery(
                    query
                ).build()
        )

        RequestAssertions.GeocodingSearchRequestAssertion(parseGeocodingRequest(json))
            .hasQueries(query)
    }

    private fun parseGeocodingRequest(requestJsonObject: Obj): GeocodingSearchRequest {
        return RequestJsonParser.parse(requestJsonObject) as GeocodingSearchRequest
    }

    private fun parseExplicitRequest(requestJsonObject: Obj): ExplicitSearchRequest {
        return RequestJsonParser.parse(requestJsonObject) as ExplicitSearchRequest
    }

    companion object {
        private val RESOLUTION = 10
        private val TILE_IDS = mapOf("RegionId123" to listOf(QuadKey("310"), QuadKey("311")))
    }
}
