package jetbrains.datalore.maps.livemap.entities.regions

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.jetbrains.livemap.LiveMapTestBase
import jetbrains.datalore.jetbrains.livemap.entities.regions.FragmentSpec
import jetbrains.datalore.maps.Utils.any
import jetbrains.datalore.maps.Utils.empty

abstract class RegionsTestBase : LiveMapTestBase() {
    fun fragmentSpecWithGeometry(regionId: String, quad: QuadKey<LonLat>): FragmentSpec {
        return FragmentSpec(regionId, quad).setGeometries(any()).withReadyEntity(componentManager)
    }

    fun emptyFragmentSpec(regionId: String, quad: QuadKey<LonLat>): FragmentSpec {
        return FragmentSpec(regionId, quad).setGeometries(empty())
    }
}