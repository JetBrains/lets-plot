/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.fragments

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.geoprotocol.GeoTile
import jetbrains.gis.geoprotocol.GeocodingService

interface FragmentProvider {

    fun getGeometries(mapObjectIds: List<String>, tileIds: Collection<QuadKey<LonLat>>): Async<Map<String, List<GeoTile>>>

    companion object {
        fun create(geocodingService: GeocodingService, mapSize: DoubleVector): FragmentProvider {
            return FragmentProviderImpl(FragmentCache(mapSize), geocodingService)
        }
    }
}