/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.viewport

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.commons.intern.spatial.calculateQuadKeys
import org.jetbrains.letsPlot.commons.intern.spatial.computeRect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Transforms.transform
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponent
import org.jetbrains.letsPlot.livemap.mapengine.MapProjection

typealias CellKey = QuadKey<World>

fun convertCellKeyToQuadKeys(mapProjection: MapProjection, cellKey: CellKey): Set<QuadKey<LonLat>> {
    val cellRect = cellKey.computeRect(mapProjection.mapRect)
    val geoRect = transform(cellRect, mapProjection::invert) ?: return emptySet()
    return calculateQuadKeys(geoRect, cellKey.length)
}

class ViewportGridStateComponent : EcsComponent {
    var visibleCells: Set<CellKey> = HashSet()
    var cellsToLoad: Set<CellKey> = HashSet()
    var cellsToRemove: Set<CellKey> = HashSet()

    val quadsRefCounter: MutableMap<QuadKey<LonLat>, Int> = HashMap()
    var quadsToLoad: Set<QuadKey<LonLat>> = HashSet()
    var quadsToRemove: Set<QuadKey<LonLat>> = HashSet()

    val visibleQuads: Set<QuadKey<LonLat>>
        get() = quadsRefCounter.keys

    fun update(newVisibleCells: Set<CellKey>) {
        val existingCells = visibleCells

        visibleCells = newVisibleCells
        cellsToLoad = visibleCells - existingCells
        cellsToRemove = existingCells - visibleCells
    }
}
