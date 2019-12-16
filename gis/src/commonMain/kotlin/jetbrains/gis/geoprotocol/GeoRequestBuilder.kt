/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.geoprotocol.GeoRequest.*
import jetbrains.gis.geoprotocol.GeoRequest.GeocodingSearchRequest.AmbiguityResolver
import jetbrains.gis.geoprotocol.GeoRequest.GeocodingSearchRequest.AmbiguityResolver.IgnoringStrategy
import jetbrains.gis.geoprotocol.GeoRequest.GeocodingSearchRequest.RegionQuery
import jetbrains.gis.geoprotocol.LevelOfDetails.Companion.fromResolution


object GeoRequestBuilder {

    private const val PARENT_KIND_ID = true

    abstract class RequestBuilderBase<T : RequestBuilderBase<T>> {
        private lateinit var mySelf: T

        abstract val mode: GeocodingMode
        protected val features: MutableSet<FeatureOption> = HashSet<FeatureOption>()

        protected var tiles: MutableMap<String, List<QuadKey<LonLat>>>? = null
            private set

        protected var levelOfDetails: LevelOfDetails? = null
            private set


        internal fun setSelf(self: T) {
            mySelf = self
        }

        abstract fun build(): GeoRequest

        fun setResolution(resolution: Int?): T {
            this.levelOfDetails = resolution?.let { fromResolution(it) }
            return mySelf
        }

        fun setTiles(keys: Map<String, List<QuadKey<LonLat>>>?): T {
            this.tiles = keys?.let { HashMap(it) }
            return mySelf
        }

        fun addTiles(id: String, keys: List<QuadKey<LonLat>>): T {
            if (tiles == null) {
                tiles = hashMapOf<String, List<QuadKey<LonLat>>>()
            }
            this.tiles!!.set(id, keys)
            return mySelf
        }

        fun addFeature(featureOption: FeatureOption): T {
            features.add(featureOption)
            return mySelf
        }

        fun setFeatures(featureOptions: List<FeatureOption>): T {
            features.clear()
            features.addAll(featureOptions)
            return mySelf
        }
    }

    class ReverseGeocodingRequestBuilder : RequestBuilderBase<ReverseGeocodingRequestBuilder>() {
        override val mode: GeocodingMode = GeocodingMode.REVERSE

        private lateinit var coordinates: List<DoubleVector>
        private lateinit var level: FeatureLevel
        private var parent: MapRegion? = null

        init {
            super.setSelf(this)
        }

        fun setCoordinates(v: List<DoubleVector>) = apply { coordinates = v }
        fun setLevel(v: FeatureLevel) = apply { level = v }
        fun setParent(v: MapRegion?) = apply { parent = v }

        override fun build(): GeoRequest {
            return MyReverseGeocodingSearchRequest(
                features,
                tiles,
                levelOfDetails,
                coordinates,
                level,
                parent
            )
        }

        internal class MyReverseGeocodingSearchRequest(
            features: Set<FeatureOption>,
            tiles: Map<String, List<QuadKey<LonLat>>>?,
            levelOfDetails: LevelOfDetails?,
            override val coordinates: List<DoubleVector>,
            override val level: FeatureLevel,
            override val parent: MapRegion?
        ) : MyGeoRequestBase(features, tiles, levelOfDetails), ReverseGeocodingSearchRequest
    }

    class GeocodingRequestBuilder : RequestBuilderBase<GeocodingRequestBuilder>() {
        override val mode: GeocodingMode = GeocodingMode.BY_NAME

        private var featureLevel: FeatureLevel? = null
        private var namesakeExampleLimit = DEFAULT_NAMESAKE_EXAMPLE_LIMIT
        private val regionQueries = ArrayList<RegionQuery>()

        init {
            super.setSelf(this)
        }

        fun addQuery(query: RegionQuery): GeocodingRequestBuilder {
            regionQueries.add(query)
            return this
        }

        fun setLevel(v: FeatureLevel?) = apply { featureLevel = v }
        fun setNamesakeExampleLimit(v: Int) = apply { namesakeExampleLimit = v }

        override fun build(): GeoRequest {
            return MyGeocodingSearchRequest(
                regionQueries,
                featureLevel,
                namesakeExampleLimit,
                features,
                tiles,
                levelOfDetails
            )
        }

        internal class MyGeocodingSearchRequest(
            override val queries: List<RegionQuery>,
            override val level: FeatureLevel?,
            override val namesakeExampleLimit: Int,
            features: Set<FeatureOption>,
            tiles: Map<String, List<QuadKey<LonLat>>>?,
            levelOfDetails: LevelOfDetails?
        ) : MyGeoRequestBase(features, tiles, levelOfDetails), GeocodingSearchRequest

        companion object {
            private const val DEFAULT_NAMESAKE_EXAMPLE_LIMIT = 10
        }
    }

    class ExplicitRequestBuilder : RequestBuilderBase<ExplicitRequestBuilder>() {
        override val mode: GeocodingMode = GeocodingMode.BY_ID

        // explicit request
        private lateinit var ids: List<String>

        init {
            super.setSelf(this)
        }

        // explicit request
        fun setIds(v: List<String>) = apply { ids = v }

        // explicit request
        override fun build(): ExplicitSearchRequest {
            return MyExplicitSearchRequest(
                ids,
                features,
                tiles,
                levelOfDetails
            )
        }

        internal class MyExplicitSearchRequest(
            override val ids: List<String>,
            features: Set<FeatureOption>,
            tiles: Map<String, List<QuadKey<LonLat>>>?,
            levelOfDetails: LevelOfDetails?
        ) : MyGeoRequestBase(features, tiles, levelOfDetails), ExplicitSearchRequest
    }


    internal open class MyGeoRequestBase(
        override val features: Set<FeatureOption>,
        override val tiles: Map<String, List<QuadKey<LonLat>>>?,
        override val levelOfDetails: LevelOfDetails?
    ) : GeoRequest

    class MapRegionBuilder {
        private lateinit var values: List<String>
        private var kind: Boolean = PARENT_KIND_ID

        fun setParentValues(v: List<String>) = apply { values = v }
        fun setParentKind(v: Boolean) = apply { kind = v }

        fun build(): MapRegion {
            return if (kind == PARENT_KIND_ID)
                MapRegion.withIdList(values)
            else
                MapRegion.withName(values[0])
        }
    }

    class RegionQueryBuilder {
        private var parent: MapRegion? = null
        private var names: List<String> = ArrayList()
        private var ambiguityResolver = AmbiguityResolver.empty()

        fun setQueryNames(v: List<String>) = apply { names = v }
        fun setQueryNames(vararg v: String) = apply { names = listOf(*v) }
        fun setParent(v: MapRegion?) = apply { parent = v }
        fun setIgnoringStrategy(v: IgnoringStrategy?) = apply { v?.let { ambiguityResolver = AmbiguityResolver.ignoring(it) } }
        fun setClosestObject(v: DoubleVector?) = apply { v?.let { ambiguityResolver = AmbiguityResolver.closestTo(it) } }
        fun setBox(v: DoubleRectangle?) = apply { v?.let { ambiguityResolver = AmbiguityResolver.within(it) } }
        fun setAmbiguityResolver(v: AmbiguityResolver) = apply { ambiguityResolver = v }

        fun build(): RegionQuery {
            return RegionQuery(names, parent, ambiguityResolver)
        }

    }

}
