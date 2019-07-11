package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.gis.geoprotocol.GeoResponse.AmbiguousGeoResponse
import jetbrains.gis.geoprotocol.GeoResponse.AmbiguousGeoResponse.*
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature


object GeoResponseBuilder {

    class SuccessResponseBuilder {
        var geocodedFeatures = List<GeocodedFeature>()
            private set

        var featureLevel: FeatureLevel? = null
            private set

        fun setGeocodedFeatures(features: List<GeocodedFeature>) = apply { this.geocodedFeatures = features }
        fun setLevel(level: FeatureLevel?) = apply { this.featureLevel = level }

        fun build() = SuccessGeoResponse(geocodedFeatures, featureLevel)
    }

    class AmbiguousResponseBuilder {
        private val myAmbiguousFeatures = ArrayList<AmbiguousFeature>()
        private var myFeatureLevel: Optional<FeatureLevel>? = null

        fun addAmbiguousFeature(feature: AmbiguousFeature): AmbiguousResponseBuilder {
            myAmbiguousFeatures.add(feature)
            return this
        }

        fun addAmbiguousFeatures(features: List<AmbiguousFeature>): AmbiguousResponseBuilder {
            features.forEach(Consumer<AmbiguousFeature> { this.addAmbiguousFeature(it) })
            return this
        }

        fun setLevel(v: Optional<FeatureLevel>): AmbiguousResponseBuilder {
            myFeatureLevel = v
            return this
        }

        fun build(): AmbiguousGeoResponse {
            return AmbiguousGeoResponse(myAmbiguousFeatures, myFeatureLevel)
        }
    }

    class GeocodedFeatureBuilder {
        private var query: String? = null
        private var id: String? = null
        private var name: String? = null
        private var centroid: DoubleVector? = null
        private var limit: GeoRectangle? = null
        private var position: GeoRectangle? = null
        private val highlights = ArrayList<String>()
        private var boundary: Geometry? = null
        private val tileGeometries = ArrayList<GeoTile>()

        fun setQuery(v: String): GeocodedFeatureBuilder {
            this.query = v
            return this
        }

        fun setId(id: String): GeocodedFeatureBuilder {
            this.id = id
            return this
        }

        fun setName(name: String): GeocodedFeatureBuilder {
            this.name = name
            return this
        }

        fun setBoundary(boundary: Geometry): GeocodedFeatureBuilder {
            this.boundary = boundary
            return this
        }

        fun addTile(tile: GeoTile): GeocodedFeatureBuilder {
            this.tileGeometries.add(tile)
            return this
        }

        fun addTiles(tiles: List<GeoTile>): GeocodedFeatureBuilder {
            tiles.forEach(Consumer<GeoTile> { this.addTile(it) })
            return this
        }

        fun setCentroid(centroid: DoubleVector): GeocodedFeatureBuilder {
            this.centroid = centroid
            return this
        }

        fun setLimit(limit: GeoRectangle): GeocodedFeatureBuilder {
            this.limit = limit
            return this
        }

        fun setPosition(position: GeoRectangle): GeocodedFeatureBuilder {
            this.position = position
            return this
        }

        fun addHighlight(v: String): GeocodedFeatureBuilder {
            this.highlights.add(v)
            return this
        }

        fun addHighlights(highlights: List<String>): GeocodedFeatureBuilder {
            highlights.forEach(Consumer<String> { this.addHighlight(it) })
            return this
        }

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
        private var query: String? = null
        private var totalNamesakeCount: Int = 0
        private val namesakeExamples = ArrayList<Namesake>()

        fun build(): AmbiguousFeature {
            return AmbiguousFeature(query, totalNamesakeCount, namesakeExamples)
        }

        fun setQuery(query: String): AmbiguousFeatureBuilder {
            this.query = query
            return this
        }

        fun setTotalNamesakeCount(totalNamesakeCount: Int): AmbiguousFeatureBuilder {
            this.totalNamesakeCount = totalNamesakeCount
            return this
        }

        fun addNamesakeExample(v: Namesake): AmbiguousFeatureBuilder {
            this.namesakeExamples.add(v)
            return this
        }

        fun addNamesakeExamples(namesakes: List<Namesake>): AmbiguousFeatureBuilder {
            namesakes.forEach(Consumer<Namesake> { this.addNamesakeExample(it) })
            return this
        }
    }

    class NamesakeBuilder {
        private var name: String? = null
        private val parentNames = ArrayList<String>()
        private val parentLevels = ArrayList<FeatureLevel>()

        fun build(): Namesake {
            if (parentNames.size != parentLevels.size) {
                throw IllegalStateException()
            }

            val namesakeParents = zip(
                parentNames.stream(),
                parentLevels.stream(),
                ???({ NamesakeParent() })
            ).collect(toList<T>())

            return Namesake(name, namesakeParents)
        }

        fun setName(name: String): NamesakeBuilder {
            this.name = name
            return this
        }

        fun addParentName(v: String): NamesakeBuilder {
            parentNames.add(v)
            return this
        }

        fun addParentLevel(v: FeatureLevel): NamesakeBuilder {
            parentLevels.add(v)
            return this
        }

        fun addParent(name: String, level: FeatureLevel): NamesakeBuilder {
            addParentName(name)
            addParentLevel(level)
            return this
        }

        fun addParents(parents: List<NamesakeParent>): NamesakeBuilder {
            parents.forEach { parent -> addParent(parent.getName(), parent.getLevel()) }
            return this
        }
    }
}
