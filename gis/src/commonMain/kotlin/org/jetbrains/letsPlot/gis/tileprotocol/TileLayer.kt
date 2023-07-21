/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.tileprotocol

data class TileLayer internal constructor(
    val name: String,
    val geometryCollection: GeometryCollection,
    val kinds: List<Int>,
    val subs: List<Int>,
    val labels: List<String?>,
    val shorts: List<String>,
    val size: Int
)
