/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.geoprotocol

import org.jetbrains.letsPlot.commons.intern.spatial.GeoRectangle
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Untyped
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.gis.geoprotocol.GeoResponse.AmbiguousGeoResponse
import org.jetbrains.letsPlot.gis.geoprotocol.GeoResponse.AmbiguousGeoResponse.*
import org.jetbrains.letsPlot.gis.geoprotocol.GeoResponse.SuccessGeoResponse
import org.jetbrains.letsPlot.gis.geoprotocol.GeoResponse.SuccessGeoResponse.*


object GeoResponseBuilder {

    class SuccessResponseBuilder {
        private val geocodingAnswers: MutableList<GeocodingAnswer> = ArrayList()
        private var featureLevel: FeatureLevel? = null

        fun addGeocodingAnswer(v: GeocodingAnswer) = apply { geocodingAnswers.add(v) }
        fun setLevel(v: FeatureLevel?) = apply { featureLevel = v }
        fun build() = SuccessGeoResponse(geocodingAnswers, featureLevel)
    }

    class AmbiguousResponseBuilder {
        private var ambiguousFeatures: ArrayList<AmbiguousFeature> = ArrayList()
        private var featureLevel: FeatureLevel? = null

        fun addAmbiguousFeature(v: AmbiguousFeature) = apply { ambiguousFeatures.add(v) }
        fun setLevel(v: FeatureLevel?) = apply { featureLevel = v }
        fun build() = AmbiguousGeoResponse(ambiguousFeatures, featureLevel)
    }

    class GeocodingAnswerBuilder {
        private val geocodedFeatures: MutableList<GeocodedFeature> = ArrayList<GeocodedFeature>()
        fun addGeocodedFeature(v: GeocodedFeature) = apply { geocodedFeatures.add(v) }
        fun addGeocodedFeatures(v: List<GeocodedFeature>) = apply { v.forEach(geocodedFeatures::add) }

        fun build(): GeocodingAnswer {
            return GeocodingAnswer(geocodedFeatures)
        }
    }

    class GeocodedFeatureBuilder() {
        private lateinit var id: String
        private lateinit var name: String
        private var centroid: Vec<Untyped>? = null
        private var limit: GeoRectangle? = null
        private var position: GeoRectangle? = null
        private var boundary: Boundary<Untyped>? = null
        private var highlights: MutableList<String> = ArrayList()
        private var fragments: MutableList<Fragment> = ArrayList()
        private val parents: MutableList<GeoParent> = ArrayList()

        fun setId(v: String) = apply { id = v }
        fun setName(v: String) = apply { name = v }
        fun setBoundary(v: Boundary<Untyped>) = apply { boundary = v }
        fun setCentroid(v: Vec<Untyped>) = apply { centroid = v }
        fun setLimit(v: GeoRectangle) = apply { limit = v }
        fun setPosition(v: GeoRectangle) = apply { position = v }
        fun addHighlight(v: String) = apply { highlights.add(v) }
        fun addFragment(v: Fragment) = apply { fragments.add(v) }
        fun addParent(v: GeoParent) = apply { parents.add(v) }
        fun addParents(v: List<GeoParent>) = apply { parents.addAll(v) }

        fun build(): GeocodedFeature {
            return GeocodedFeature(
                id,
                name,
                centroid,
                position,
                limit,
                boundary,
                highlights.ifEmpty { null },
                fragments.ifEmpty { null },
                parents.ifEmpty { null }
            )
        }
    }

    class AmbiguousFeatureBuilder {
        private lateinit var query: String
        private var totalNamesakeCount: Int = 0
        private var namesakeExamples: ArrayList<Namesake> = ArrayList()

        fun setQuery(v: String) = apply { query = v }
        fun addNamesakeExample(v: Namesake) = apply { namesakeExamples.add(v) }
        fun setTotalNamesakeCount(v: Int) = apply { totalNamesakeCount = v }
        fun build() = AmbiguousFeature(query, totalNamesakeCount, namesakeExamples)
    }

    class NamesakeBuilder {
        private lateinit var name: String
        private val parentNames = ArrayList<String>()
        private val parentLevels = ArrayList<FeatureLevel>()

        fun setName(v: String) = apply { name = v }
        fun addParentName(v: String) = apply { parentNames.add(v) }
        fun addParentLevel(v: FeatureLevel) = apply { parentLevels.add(v) }

        fun build(): Namesake {
            if (parentNames.size != parentLevels.size) {
                throw IllegalStateException()
            }

            return Namesake(
                name,
                parentNames.zip(parentLevels, ::NamesakeParent).toList()
            )
        }
    }
}
