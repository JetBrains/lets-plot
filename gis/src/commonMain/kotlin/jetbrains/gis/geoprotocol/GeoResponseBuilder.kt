/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.projectionGeometry.Generic
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.gis.geoprotocol.GeoResponse.AmbiguousGeoResponse
import jetbrains.gis.geoprotocol.GeoResponse.AmbiguousGeoResponse.*
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature


object GeoResponseBuilder {

    class SuccessResponseBuilder {
        private var geocodedFeatures: ArrayList<GeocodedFeature> = ArrayList()
        private var featureLevel: FeatureLevel? = null

        fun addGeocodedFeature(v: GeocodedFeature) = apply { geocodedFeatures.add(v) }
        fun setLevel(v: FeatureLevel?) = apply { featureLevel = v }
        fun build() = SuccessGeoResponse(geocodedFeatures, featureLevel)
    }

    class AmbiguousResponseBuilder {
        private var ambiguousFeatures: ArrayList<AmbiguousFeature> = ArrayList()
        private var featureLevel: FeatureLevel? = null

        fun addAmbiguousFeature(v: AmbiguousFeature) = apply { ambiguousFeatures.add(v) }
        fun setLevel(v: FeatureLevel?) = apply { featureLevel = v }
        fun build() = AmbiguousGeoResponse(ambiguousFeatures, featureLevel)
    }

    class GeocodedFeatureBuilder() {
        private lateinit var query: String
        private lateinit var id: String
        private lateinit var name: String
        private var centroid: Vec<Generic>? = null
        private var limit: GeoRectangle? = null
        private var position: GeoRectangle? = null
        private var boundary: Boundary<Generic>? = null
        private var highlights: MutableList<String> = ArrayList()
        private var tileGeometries: ArrayList<GeoTile> = ArrayList()

        fun setQuery(v: String) = apply { query = v }
        fun setId(v: String) = apply { id = v }
        fun setName(v: String) = apply { name = v }
        fun setBoundary(v: Boundary<Generic>) = apply { boundary = v }
        fun setCentroid(v: Vec<Generic>) = apply { centroid = v }
        fun setLimit(v: GeoRectangle) = apply { limit = v }
        fun setPosition(v: GeoRectangle) = apply { position = v }
        fun addHighlight(v: String) = apply { highlights.add(v) }
        fun addTile(v: GeoTile) = apply { tileGeometries.add(v) }

        fun build(): GeocodedFeature {
            return GeocodedFeature(
                query,
                id,
                name,
                centroid,
                position,
                limit,
                boundary,
                highlights.ifEmpty { null },
                tileGeometries.ifEmpty { null }
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
