/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart.fragment

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.base.spatial.zoom


data class FragmentKey(val regionId: String, val quadKey: QuadKey<LonLat>) {
    fun zoom() = quadKey.zoom()
}