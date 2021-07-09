/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.basemap.vector

import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.livemap.viewport.CellKey
import jetbrains.livemap.core.multitasking.MicroTask

internal interface TileDataParser {
    fun parse(cellKey: CellKey, tileData: List<TileLayer>): MicroTask<Map<String, List<TileFeature>>>
}