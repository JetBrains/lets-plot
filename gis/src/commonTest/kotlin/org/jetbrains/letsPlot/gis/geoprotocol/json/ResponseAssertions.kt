/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.geoprotocol.json

import org.jetbrains.letsPlot.gis.geoprotocol.FeatureLevel
import org.jetbrains.letsPlot.gis.geoprotocol.GeoResponse.*
import org.jetbrains.letsPlot.gis.geoprotocol.GeoResponse.AmbiguousGeoResponse.AmbiguousFeature
import org.jetbrains.letsPlot.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature
import kotlin.test.assertEquals

internal object ResponseAssertions {
    fun assertThatSuccessResponse(response: SuccessGeoResponse): SuccessResponseAssertion {
        return SuccessResponseAssertion(response)
    }

    fun assertThatErrorResponse(response: ErrorGeoResponse): ErrorResponseAssertion {
        return ErrorResponseAssertion(response)
    }

    fun assertThatAmbiguousResponse(response: AmbiguousGeoResponse): AmbiguousResponseAssertion {
        return AmbiguousResponseAssertion(response)
    }

    internal class SuccessResponseAssertion(private val actual: SuccessGeoResponse) {

        fun hasFeatures(vararg features: GeocodedFeature): SuccessResponseAssertion {
            assertEquals(features.size, actual.answers.flatMap { it.geocodedFeatures }.size)

            actual.answers.flatMap { it.geocodedFeatures }.forEachIndexed { index, actualFeature ->
                val expectedFeature = features[index]
                assertEquals(expectedFeature, actualFeature)
            }
            return this
        }

        fun hasFeatureLevel(expected: FeatureLevel?): SuccessResponseAssertion {
            assertEquals(expected, actual.featureLevel)
            return this
        }
    }

    internal class ErrorResponseAssertion(private val actual: ErrorGeoResponse) {

        fun hasMessage(message: String): ErrorResponseAssertion {
            assertEquals(message, actual.message)
            return this
        }
    }

    internal class AmbiguousResponseAssertion(private val actual: AmbiguousGeoResponse) {

        fun hasAmbiguousFeatures(vararg features: AmbiguousFeature): AmbiguousResponseAssertion {
            assertEquals(features.size, actual.features.size)

            for (i in 0 until actual.features.size) {
                val actualFeature = actual.features.get(i)
                val expectedFeature = features[i]
                assertEquals(expectedFeature, actualFeature)
            }
            return this
        }

        fun hasFeatureLevel(expected: FeatureLevel?): AmbiguousResponseAssertion {
            assertEquals(expected, actual.featureLevel)
            return this
        }
    }
}
