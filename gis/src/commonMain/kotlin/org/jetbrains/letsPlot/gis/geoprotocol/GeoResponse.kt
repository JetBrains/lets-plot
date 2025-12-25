/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.geoprotocol

import org.jetbrains.letsPlot.commons.intern.spatial.GeoRectangle
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Untyped
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec

interface GeoResponse {

    data class SuccessGeoResponse(
        val answers: List<GeocodingAnswer>,
        val featureLevel: FeatureLevel?
    ) : GeoResponse {

        data class GeoParent(val id: String, val name: String, val level: FeatureLevel)

        data class GeocodingAnswer(val geocodedFeatures: List<GeocodedFeature>)

        data class GeocodedFeature(
            val id: String,
            val name: String,
            val centroid: Vec<Untyped>?,
            val position: GeoRectangle?,
            val limit: GeoRectangle?,
            val boundary: Boundary<Untyped>?,
            val highlights: List<String>?,
            val fragments: List<Fragment>?,
            val parents: List<GeoParent>?
        )
    }

    data class ErrorGeoResponse(val message: String) : GeoResponse

    data class AmbiguousGeoResponse(
        val features: List<AmbiguousFeature>,
        val featureLevel: FeatureLevel?
    ) : GeoResponse {

        data class AmbiguousFeature(
            val request: String,
            val namesakeCount: Int,
            val namesakes: List<Namesake>
        )

        data class Namesake(val name: String, val parents: List<NamesakeParent>)

        data class NamesakeParent(val name: String, val level: FeatureLevel)
    }
}
