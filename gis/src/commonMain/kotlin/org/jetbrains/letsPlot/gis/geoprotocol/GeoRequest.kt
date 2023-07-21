/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.geoprotocol


import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey


interface GeoRequest {

    val features: Set<FeatureOption>
    val fragments: Map<String, List<QuadKey<LonLat>>>?
    val levelOfDetails: LevelOfDetails?

    enum class FeatureOption private constructor(private val myValue: String) {
        HIGHLIGHTS("highlights"),
        POSITION("position"),
        CENTROID("centroid"),
        LIMIT("limit"),
        BOUNDARY("boundary"),
        FRAGMENTS("tiles");

        override fun toString(): String {
            return myValue
        }
    }


    interface ExplicitSearchRequest : GeoRequest {
        val ids: List<String>
    }
}
