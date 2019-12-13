/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.regions

import jetbrains.datalore.base.geospatial.LonLat
import jetbrains.datalore.base.geospatial.QuadKey
import jetbrains.datalore.base.geospatial.zoom


data class FragmentKey(val regionId: String, val quadKey: QuadKey<LonLat>) {
    fun zoom() = quadKey.zoom()
}