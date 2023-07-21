/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.geoprotocol.json

import org.jetbrains.letsPlot.commons.intern.json.Obj
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.gis.geoprotocol.GeoRequest.FeatureOption
import org.jetbrains.letsPlot.gis.geoprotocol.GeoRequestBuilder.ExplicitRequestBuilder
import org.jetbrains.letsPlot.gis.geoprotocol.GeoRequestBuilder.RequestBuilderBase
import org.jetbrains.letsPlot.gis.geoprotocol.GeocodingMode.BY_ID
import org.jetbrains.letsPlot.gis.geoprotocol.GeocodingMode.BY_NAME
import org.jetbrains.letsPlot.gis.geoprotocol.MapRegion
import org.jetbrains.letsPlot.gis.geoprotocol.json.RequestJsonFormatter

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

    private fun explicitRequestBuilder(): ExplicitRequestBuilder {
        return myRequestBuilder as ExplicitRequestBuilder
    }

    fun featureOptions(vararg v: FeatureOption) = apply { v.forEach { myRequestBuilder.addFeature(it) } }
    fun resolution(v: Int?) = apply { myRequestBuilder.setResolution(v) }
    fun fragments(v: Map<String, List<QuadKey<LonLat>>>?) = apply { myRequestBuilder.setFragments(v) }
    fun highlights() = apply { myRequestBuilder.addFeature(FeatureOption.HIGHLIGHTS) }
    fun position() = apply { myRequestBuilder.addFeature(FeatureOption.POSITION) }
    fun centroid() = apply { myRequestBuilder.addFeature(FeatureOption.CENTROID) }
    fun limit() = apply { myRequestBuilder.addFeature(FeatureOption.LIMIT) }
    fun boundary() = apply { myRequestBuilder.addFeature(FeatureOption.BOUNDARY) }

    fun regionId(vararg ids: String) = apply { myParent = MapRegion.withIdList(listOf(*ids)) }
    fun region(name: String) = apply { myParent = MapRegion.withName(name) }

    fun build(): Obj {
        if (myNames != null && myNames.size > 1) {
            throw IllegalStateException("TODO")
        }

        if (myRequestBuilder.mode === BY_ID) {
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
    }
}
