package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.gis.geoprotocol.GeoResponse.AmbiguousGeoResponse
import jetbrains.gis.geoprotocol.GeoResponse.AmbiguousGeoResponse.*
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature


object GeoResponseBuilder {

    class SuccessResponseBuilder {
        private var geocodedFeatures: List<GeocodedFeature> = emptyList()
        private var featureLevel: FeatureLevel? = null

        fun setGeocodedFeatures(v: List<GeocodedFeature>) = apply { geocodedFeatures = v }
        fun setLevel(v: FeatureLevel?) = apply { featureLevel = v }
        fun build() = SuccessGeoResponse(geocodedFeatures, featureLevel)
    }

    class AmbiguousResponseBuilder {
        private var ambiguousFeatures: List<AmbiguousFeature> = emptyList()
        private var featureLevel: FeatureLevel? = null

        fun setAmbiguousFeatures(v: List<AmbiguousFeature>) = apply { ambiguousFeatures = v }
        fun setLevel(v: FeatureLevel?) = apply { featureLevel = v }
        fun build() = AmbiguousGeoResponse(ambiguousFeatures, featureLevel)
    }

    class GeocodedFeatureBuilder(query: String, id: String, name: String) {
        private val query: String
        private val id: String
        private val name: String
        private var centroid: DoubleVector? = null
        private var limit: GeoRectangle? = null
        private var position: GeoRectangle? = null
        private var highlights: List<String>? = null
        private var boundary: Geometry? = null
        private var tileGeometries: List<GeoTile>? = null

        init {
            this.query = query
            this.id = id
            this.name = name
        }

        fun setBoundary(v: Geometry) = apply { boundary = v }
        fun setTiles(v: List<GeoTile>) = apply { tileGeometries = v }
        fun setCentroid(v: DoubleVector) = apply { centroid = v }
        fun setLimit(v: GeoRectangle) = apply { limit = v }
        fun setPosition(v: GeoRectangle) = apply { position = v }
        fun addHighlight(v: List<String>) = apply { highlights = v }

        fun build(): GeocodedFeature {
            return GeocodedFeature(
                query,
                id,
                name,
                highlights,
                centroid,
                position,
                limit,
                boundary,
                tileGeometries
            )
        }
    }

    class AmbiguousFeatureBuilder {
        private lateinit var query: String
        private var totalNamesakeCount: Int = 0
        private var namesakeExamples: List<Namesake> = emptyList()

        fun setQuery(v: String) = apply { query = v }
        fun setTotalNamesakeCount(v: Int) = apply { totalNamesakeCount = v }
        fun addNamesakeExamples(v: List<Namesake>) = apply { namesakeExamples = v }
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
