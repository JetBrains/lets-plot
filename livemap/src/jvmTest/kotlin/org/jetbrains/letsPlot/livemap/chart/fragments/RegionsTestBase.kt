/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.fragments

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.livemap.LiveMapTestBase
import org.jetbrains.letsPlot.livemap.Utils

abstract class RegionsTestBase : org.jetbrains.letsPlot.livemap.LiveMapTestBase() {
    fun fragmentSpecWithGeometry(regionId: String, quad: QuadKey<LonLat>): FragmentSpec {
        return FragmentSpec(regionId, quad).setGeometries(Utils.any()).withReadyEntity(componentManager)
    }

    fun emptyFragmentSpec(regionId: String, quad: QuadKey<LonLat>): FragmentSpec {
        return FragmentSpec(regionId, quad).setGeometries(Utils.empty())
    }
}