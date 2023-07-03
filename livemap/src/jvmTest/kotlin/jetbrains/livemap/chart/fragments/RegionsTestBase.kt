/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.chart.fragments

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.jetbrains.livemap.LiveMapTestBase
import jetbrains.datalore.maps.Utils

abstract class RegionsTestBase : LiveMapTestBase() {
    fun fragmentSpecWithGeometry(regionId: String, quad: QuadKey<LonLat>): FragmentSpec {
        return FragmentSpec(regionId, quad).setGeometries(Utils.any()).withReadyEntity(componentManager)
    }

    fun emptyFragmentSpec(regionId: String, quad: QuadKey<LonLat>): FragmentSpec {
        return FragmentSpec(regionId, quad).setGeometries(Utils.empty())
    }
}