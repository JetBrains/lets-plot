/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.basemap.vector

import jetbrains.datalore.base.async.Async
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.livemap.viewport.CellKey

internal interface TileDataFetcher {
    fun fetch(cellKey: CellKey): Async<List<TileLayer>>
}