/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol.json

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import jetbrains.gis.geoprotocol.FeatureLevel
import jetbrains.gis.geoprotocol.GeoRequest
import jetbrains.gis.geoprotocol.GeoRequest.*
import jetbrains.gis.geoprotocol.LevelOfDetails
import jetbrains.gis.geoprotocol.MapRegion
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal object RequestAssertions {

    fun assertThatExplicitRequest(request: ExplicitSearchRequest): ExplicitSearchRequestAssertion {
        return ExplicitSearchRequestAssertion(request)
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

        fun hasFragments(expected: Map<String, List<QuadKey<LonLat>>>): TAssertion {
            assertEquals(expected, actual.fragments)
            return mySelf
        }

        fun hasNoFragments(): TAssertion {
            assertTrue(actual.fragments == null || actual.fragments!!.isEmpty())
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
}
