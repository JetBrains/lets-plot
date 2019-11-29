/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.gis.geoprotocol.GeoResponse
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.entities.regions.RegionIdComponent

fun <T> getGeocodingDataMap(
    features: List<GeoResponse.SuccessGeoResponse.GeocodedFeature>,
    getData: (GeoResponse.SuccessGeoResponse.GeocodedFeature) -> T
): MutableMap<String, T> {
    val dataMap = HashMap<String, T>(features.size)
    for (feature in features) {
        dataMap[feature.request] = getData(feature)
    }
    return dataMap
}

val EcsEntity.regionId
    get() = get<RegionIdComponent>().regionId