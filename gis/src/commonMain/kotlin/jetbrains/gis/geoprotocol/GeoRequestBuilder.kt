/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.geoprotocol.GeoRequest.*
import jetbrains.gis.geoprotocol.LevelOfDetails.Companion.fromResolution


object GeoRequestBuilder {

    private const val PARENT_KIND_ID = true

    abstract class RequestBuilderBase<T : RequestBuilderBase<T>> {
        private lateinit var mySelf: T

        abstract val mode: GeocodingMode
        protected val features: MutableSet<FeatureOption> = HashSet<FeatureOption>()

        protected var fragments: MutableMap<String, List<QuadKey<LonLat>>>? = null
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

        fun setFragments(keys: Map<String, List<QuadKey<LonLat>>>?): T {
            this.fragments = keys?.let { HashMap(it) }
            return mySelf
        }

        fun addFragments(id: String, keys: List<QuadKey<LonLat>>): T {
            if (fragments == null) {
                fragments = hashMapOf<String, List<QuadKey<LonLat>>>()
            }
            this.fragments!!.set(id, keys)
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
                fragments,
                levelOfDetails
            )
        }

        internal class MyExplicitSearchRequest(
            override val ids: List<String>,
            features: Set<FeatureOption>,
            fragments: Map<String, List<QuadKey<LonLat>>>?,
            levelOfDetails: LevelOfDetails?
        ) : MyGeoRequestBase(features, fragments, levelOfDetails), ExplicitSearchRequest
    }


    internal open class MyGeoRequestBase(
        override val features: Set<FeatureOption>,
        override val fragments: Map<String, List<QuadKey<LonLat>>>?,
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
}
