package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.gis.geoprotocol.GeoRequest.*
import jetbrains.gis.geoprotocol.GeoRequest.GeocodingSearchRequest.AmbiguityResolver
import jetbrains.gis.geoprotocol.GeoRequest.GeocodingSearchRequest.AmbiguityResolver.IgnoringStrategy
import jetbrains.gis.geoprotocol.GeoRequest.GeocodingSearchRequest.RegionQuery
import jetbrains.gis.geoprotocol.LevelOfDetails.Companion.fromResolution


object GeoRequestBuilder {

    private val PARENT_KIND_ID = true

    abstract class ReverseGeocodingRequestBuilder : RequestBuilderBase<ReverseGeocodingRequestBuilder>() {
        abstract var myCoordinates: List<DoubleVector>
        abstract var myLevel: FeatureLevel
        private var myParent: MapRegion? = null

        override val mode: GeocodingMode
            get() = GeocodingMode.REVERSE

        init {
            super.setSelf(this)
        }

        override fun build(): GeoRequest {
            return MyReverseGeocodingSearchRequest(
                features,
                getTiles(),
                levelOfDetails,
                myCoordinates,
                myLevel,
                myParent
            )
        }

        fun setCoordinates(coordinates: List<DoubleVector>): ReverseGeocodingRequestBuilder {
            myCoordinates = coordinates
            return this
        }

        fun setLevel(level: FeatureLevel): ReverseGeocodingRequestBuilder {
            myLevel = level
            return this
        }

        fun setParent(parent: MapRegion?): ReverseGeocodingRequestBuilder {
            myParent = parent
            return this
        }

        internal class MyReverseGeocodingSearchRequest(
            features: List<FeatureOption>,
            tiles: Map<String, List<QuadKey>>?,
            levelOfDetails: LevelOfDetails?,
            override val coordinates: List<DoubleVector>,
            override val level: FeatureLevel,
            override val parent: MapRegion?
        ) : MyGeoRequestBase(features, tiles, levelOfDetails), ReverseGeocodingSearchRequest
    }

    class GeocodingRequestBuilder : RequestBuilderBase<GeocodingRequestBuilder>() {
        private var myFeatureLevel: FeatureLevel? = null
        private var myNamesakeExampleLimit = DEFAULT_NAMESAKE_EXAMPLE_LIMIT
        private val myRegionQueries = ArrayList<RegionQuery>()

        override val mode: GeocodingMode
            get() = GeocodingMode.BY_NAME

        init {
            super.setSelf(this)
        }

        override fun build(): GeoRequest {
            return MyGeocodingSearchRequest(
                myRegionQueries,
                myFeatureLevel,
                myNamesakeExampleLimit,
                features,
                getTiles(),
                levelOfDetails
            )
        }

        fun addQuery(query: RegionQuery): GeocodingRequestBuilder {
            myRegionQueries.add(query)
            return this
        }

        fun setLevel(featureLevel: FeatureLevel?): GeocodingRequestBuilder {
            myFeatureLevel = featureLevel
            return this
        }

        fun setNamesakeExampleLimit(limit: Int): GeocodingRequestBuilder {
            myNamesakeExampleLimit = limit
            return this
        }

        internal class MyGeocodingSearchRequest(
            override val queries: List<RegionQuery>,
            override val level: FeatureLevel?,
            override val namesakeExampleLimit: Int,
            features: List<FeatureOption>,
            tiles: Map<String, List<QuadKey>>?,
            levelOfDetails: LevelOfDetails?
        ) : MyGeoRequestBase(features, tiles, levelOfDetails), GeocodingSearchRequest

        companion object {
            private const val DEFAULT_NAMESAKE_EXAMPLE_LIMIT = 10
        }
    }

    abstract class ExplicitRequestBuilder : RequestBuilderBase<ExplicitRequestBuilder>() {
        // explicit request
        abstract var ids: List<String>

        override val mode: GeocodingMode
            get() = GeocodingMode.BY_ID

        init {
            super.setSelf(this)
        }

        // explicit request
        override fun build(): ExplicitSearchRequest {
            return MyExplicitSearchRequest(
                ids,
                features,
                getTiles(),
                levelOfDetails
            )
        }

        // explicit request
        fun setIds(ids: List<String>): ExplicitRequestBuilder {
            this.ids = ids
            return this
        }

        internal class MyExplicitSearchRequest(
            override val ids: List<String>, features: List<FeatureOption>, tiles: Map<String, List<QuadKey>>?,
            levelOfDetails: LevelOfDetails?
        ) : MyGeoRequestBase(features, tiles, levelOfDetails), ExplicitSearchRequest
    }

    abstract class RequestBuilderBase<T : RequestBuilderBase<*>> {
        private var mySelf: T? = null

        val features: MutableList<FeatureOption> = ArrayList<FeatureOption>()
        private var myTiles: Map<String, List<QuadKey>>? = null
        internal var levelOfDetails: LevelOfDetails? = null
            private set

        abstract val mode: GeocodingMode

        internal fun setSelf(self: T) {
            mySelf = self
        }

        abstract fun build(): GeoRequest

        fun setResolution(resolution: Int?): T? {
            this.levelOfDetails = resolution.map(Function<Int, LevelOfDetails> { fromResolution() })
            return mySelf
        }

        fun getTiles(): Map<String, List<QuadKey>>? {
            return myTiles
        }

        fun setTiles(keys: Map<String, List<QuadKey>>?): T? {
            this.myTiles = keys
            return mySelf
        }

        fun addTiles(id: String, keys: List<QuadKey>): T? {
            if (!myTiles.isPresent()) {
                myTiles = hashMapOf<String, List<QuadKey>>()
            }
            this.myTiles!!.set(id, keys)
            return mySelf
        }

        fun addFeature(featureOption: FeatureOption): T? {
            features.add(featureOption)
            return mySelf
        }

        fun setFeatures(featureOptions: List<FeatureOption>): T? {
            features.clear()
            features.addAll(featureOptions)
            return mySelf
        }
    }


    internal open class MyGeoRequestBase(
        val features: List<FeatureOption>,
        val tiles: Optional<Map<String, List<QuadKey>>>,
        val levelOfDetails: Optional<LevelOfDetails>
    ) : GeoRequest

    class MapRegionBuilder {
        private var myValues: List<String>? = null
        private var myKind: Boolean? = null

        fun setParentValues(values: List<String>): MapRegionBuilder {
            myValues = values
            return this
        }

        fun setParentKind(kind: Boolean): MapRegionBuilder {
            myKind = kind
            return this
        }

        fun build(): MapRegion {
            return if (myKind == PARENT_KIND_ID)
                MapRegion.withIdList(myValues)
            else
                MapRegion.withName(myValues!![0])
        }
    }

    class RegionQueryBuilder {
        private var myParent: MapRegion? = null
        private var myNames: List<String> = ArrayList()
        private var myAmbiguityResolver = AmbiguityResolver.empty()

        fun setQueryNames(names: List<String>): RegionQueryBuilder {
            myNames = names
            return this
        }

        fun setQueryNames(vararg names: String): RegionQueryBuilder {
            return setQueryNames(asList<String>(*names))
        }

        fun setParent(parent: MapRegion?): RegionQueryBuilder {
            myParent = parent
            return this
        }

        fun setIgnoringStrategy(v: IgnoringStrategy?): RegionQueryBuilder {
            v?.let { myAmbiguityResolver = AmbiguityResolver.ignoring(it) }
            return this
        }

        fun setClosestObject(v: DoubleVector?): RegionQueryBuilder {
            v?.let { myAmbiguityResolver = AmbiguityResolver.closestTo(it) }
            return this
        }

        fun setBox(v: DoubleRectangle?): RegionQueryBuilder {
            v?.let { myAmbiguityResolver = AmbiguityResolver.within(it) }
            return this
        }

        fun build(): RegionQuery {
            return RegionQuery(myNames, myParent, myAmbiguityResolver)
        }

        fun setAmbiguityResolver(ambiguityResolver: AmbiguityResolver): RegionQueryBuilder {
            myAmbiguityResolver = ambiguityResolver
            return this
        }
    }

}
