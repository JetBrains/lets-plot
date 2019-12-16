/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.geoprotocol.FeatureLevel
import jetbrains.gis.geoprotocol.GeoRequest
import jetbrains.gis.geoprotocol.GeoRequest.*
import jetbrains.gis.geoprotocol.GeoRequest.GeocodingSearchRequest.RegionQuery
import jetbrains.gis.geoprotocol.LevelOfDetails
import jetbrains.gis.geoprotocol.MapRegion
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal object RequestAssertions {

    fun assertThatExplicitRequest(request: ExplicitSearchRequest): ExplicitSearchRequestAssertion {
        return ExplicitSearchRequestAssertion(request)
    }

    fun assertThatReverseRequest(request: ReverseGeocodingSearchRequest): ReverseGeocodingSearchRequestAssertion {
        return ReverseGeocodingSearchRequestAssertion(request)
    }

    fun assertThatGeocodingRequest(request: GeocodingSearchRequest): GeocodingSearchRequestAssertion {
        return GeocodingSearchRequestAssertion(request)
    }

    internal open class SearchRequestBaseAssertion
    <TAssertion : SearchRequestBaseAssertion<TAssertion, T>, T : GeoRequest>(val actual: T) {

        private lateinit var mySelf: TAssertion

        fun setSelf(self: TAssertion) {
            mySelf = self
        }

        fun hasFeatures(vararg featureOptions: FeatureOption): TAssertion {
            assertEquals(featureOptions.toSet(), actual.features)
            return mySelf
        }

        fun hasNoFeatures(): TAssertion {
            assertTrue(actual.features.isEmpty())
            return mySelf
        }

        fun hasTiles(expected: Map<String, List<QuadKey<LonLat>>>): TAssertion {
            assertEquals(expected, actual.tiles)
            return mySelf
        }

        fun hasNoTiles(): TAssertion {
            assertTrue(actual.tiles == null || actual.tiles!!.isEmpty())
            return mySelf
        }

        fun hasLevelOfDetails(expected: LevelOfDetails): TAssertion {
            assertEquals(expected, actual.levelOfDetails)
            return mySelf
        }

        fun hasNoLevelOfDetails(): TAssertion {
            assertTrue(actual.levelOfDetails == null)
            return mySelf
        }
    }

    internal class ExplicitSearchRequestAssertion(request: ExplicitSearchRequest) :
        SearchRequestBaseAssertion<ExplicitSearchRequestAssertion, ExplicitSearchRequest>
            (request) {

        init {
            setSelf(this)
        }

        fun hasIds(ids: List<String>): ExplicitSearchRequestAssertion {
            assertEquals(ids, actual.ids)
            return this
        }
    }


    internal class ReverseGeocodingSearchRequestAssertion(request: ReverseGeocodingSearchRequest) :
        SearchRequestBaseAssertion<ReverseGeocodingSearchRequestAssertion, ReverseGeocodingSearchRequest>
            (request) {

        init {
            setSelf(this)
        }

        fun hasCoordinates(vararg v: DoubleVector): ReverseGeocodingSearchRequestAssertion {
            assertEquals(listOf(*v), actual.coordinates)
            return this
        }

        fun hasLevel(v: FeatureLevel): ReverseGeocodingSearchRequestAssertion {
            assertEquals(v, actual.level)
            return this
        }

        fun hasParent(v: MapRegion): ReverseGeocodingSearchRequestAssertion {
            assertEquals(v, actual.parent)
            return this
        }

        fun hasNoParent(): ReverseGeocodingSearchRequestAssertion {
            assertNull(actual.parent)
            return this
        }

    }

    internal class GeocodingSearchRequestAssertion(request: GeocodingSearchRequest) :
        SearchRequestBaseAssertion<GeocodingSearchRequestAssertion, GeocodingSearchRequest>(request) {

        init {
            setSelf(this)
        }

        fun hasQueries(vararg v: RegionQuery): GeocodingSearchRequestAssertion {
            assertEquals(listOf(*v), actual.queries)
            return this
        }

        fun hasLevel(v: FeatureLevel): GeocodingSearchRequestAssertion {
            assertEquals(v, actual.level)
            return this
        }

        fun hasNoLevel(): GeocodingSearchRequestAssertion {
            assertNull(actual.level)
            return this
        }

        fun hasNamesakeExampleLimit(v: Int): GeocodingSearchRequestAssertion {
            assertEquals(v, actual.namesakeExampleLimit)
            return this
        }
    }


}
