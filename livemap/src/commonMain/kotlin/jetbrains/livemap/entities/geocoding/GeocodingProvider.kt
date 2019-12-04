/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.datalore.base.async.Async
import jetbrains.gis.geoprotocol.*
import jetbrains.gis.geoprotocol.GeoRequest.FeatureOption
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature

class GeocodingProvider(
    private val myGeocodingService: GeocodingService,
    private val myFeatureLevel: FeatureLevel?,
    private val myParent: MapRegion?
) {

    fun getRegionsIdByNames(names: List<String>): Async<List<GeocodedFeature>> {
        return GeoRequestBuilder.GeocodingRequestBuilder()
            .setLevel(myFeatureLevel)
            .addQuery(
                GeoRequestBuilder.RegionQueryBuilder()
                    .setQueryNames(names)
                    .setParent(myParent)
                    .build()
            )
            .build()
            .run(myGeocodingService::execute)
    }

    fun featuresByRegionIds(regionIds: List<String>, featureOptions: List<FeatureOption>): Async<List<GeocodedFeature>> {
        return GeoRequestBuilder.ExplicitRequestBuilder()
            .setIds(regionIds)
            .setFeatures(featureOptions)
            .build()
            .run(myGeocodingService::execute)
    }
}