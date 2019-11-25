/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.common.json.Obj
import jetbrains.gis.geoprotocol.FeatureLevel
import jetbrains.gis.geoprotocol.GeoRequest.FeatureOption
import jetbrains.gis.geoprotocol.GeoRequestBuilder.ExplicitRequestBuilder
import jetbrains.gis.geoprotocol.GeoRequestBuilder.GeocodingRequestBuilder
import jetbrains.gis.geoprotocol.GeoRequestBuilder.RegionQueryBuilder
import jetbrains.gis.geoprotocol.GeoRequestBuilder.RequestBuilderBase
import jetbrains.gis.geoprotocol.GeocodingMode.BY_ID
import jetbrains.gis.geoprotocol.GeocodingMode.BY_NAME
import jetbrains.gis.geoprotocol.MapRegion

class RequestJsonBuilder private constructor(private val myRequestBuilder: RequestBuilderBase<*>, names: List<String>) {
    private val myNames: List<String>?
    private val myIds: List<String>?
    private var myParent: MapRegion? = null

    init {
        when {
            myRequestBuilder.mode === BY_NAME -> {
                myNames = names
                myIds = null
            }
            myRequestBuilder.mode === BY_ID -> {
                myNames = null
                myIds = names
            }
            else -> throw IllegalStateException("Unknown mode")
        }

    }

    private fun geocodingRequestBuilder(): GeocodingRequestBuilder {
        return myRequestBuilder as GeocodingRequestBuilder
    }

    private fun explicitRequestBuilder(): ExplicitRequestBuilder {
        return myRequestBuilder as ExplicitRequestBuilder
    }

    fun featureOptions(vararg v: FeatureOption) = apply { v.forEach { myRequestBuilder.addFeature(it) } }
    fun resolution(v: Int?) = apply { myRequestBuilder.setResolution(v) }
    fun tiles(v: Map<String, List<QuadKey<LonLat>>>?) = apply { myRequestBuilder.setTiles(v) }
    fun highlights() = apply { myRequestBuilder.addFeature(FeatureOption.HIGHLIGHTS) }
    fun position() = apply { myRequestBuilder.addFeature(FeatureOption.POSITION) }
    fun centroid() = apply { myRequestBuilder.addFeature(FeatureOption.CENTROID) }
    fun limit() = apply { myRequestBuilder.addFeature(FeatureOption.LIMIT) }
    fun boundary() = apply { myRequestBuilder.addFeature(FeatureOption.BOUNDARY) }

    fun level(v: FeatureLevel?) = apply { geocodingRequestBuilder().setLevel(v) }
    fun autoDetectLevel() = apply { geocodingRequestBuilder().setLevel(null) }
    fun country() = apply { geocodingRequestBuilder().setLevel(FeatureLevel.COUNTRY) }
    fun state() = apply { geocodingRequestBuilder().setLevel(FeatureLevel.STATE) }
    fun county() = apply { geocodingRequestBuilder().setLevel(FeatureLevel.COUNTY) }
    fun city() = apply { geocodingRequestBuilder().setLevel(FeatureLevel.CITY) }
    fun regionId(vararg ids: String) = apply { myParent = MapRegion.withIdList(listOf(*ids)) }
    fun region(name: String) = apply { myParent = MapRegion.withName(name) }
    fun namesakeExampleLimit(v: Int) = apply { geocodingRequestBuilder().setNamesakeExampleLimit(v) }

    fun build(): Obj {
        if (myNames != null && myNames.size > 1) {
            throw IllegalStateException("TODO")
        }

        if (myRequestBuilder.mode === BY_NAME) {
            geocodingRequestBuilder().addQuery(
                RegionQueryBuilder()
                    .setQueryNames(myNames!!)
                    .setParent(myParent)
                    .build()
            )
        } else if (myRequestBuilder.mode === BY_ID) {
            explicitRequestBuilder().setIds(myIds!!)
        } else {
            throw IllegalStateException("Unkown mode")
        }

        return RequestJsonFormatter.format(myRequestBuilder.build())
    }

    companion object {
        internal fun explicit(vararg ids: String): RequestJsonBuilder {
            val requestBuilder = ExplicitRequestBuilder()
            return RequestJsonBuilder(requestBuilder, listOf(*ids))
        }

        fun geocoding(vararg names: String): RequestJsonBuilder {
            val requestBuilder = GeocodingRequestBuilder()
            return RequestJsonBuilder(requestBuilder, listOf(*names))
        }
    }
}
