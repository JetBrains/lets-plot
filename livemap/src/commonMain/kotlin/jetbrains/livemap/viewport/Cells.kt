/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.viewport

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.base.spatial.calculateQuadKeys
import jetbrains.datalore.base.spatial.computeRect
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.projections.ProjectionUtil
import jetbrains.livemap.projection.MapProjection
import jetbrains.livemap.projection.World

typealias CellKey = QuadKey<World>

fun convertCellKeyToQuadKeys(mapProjection: MapProjection, cellKey: CellKey): Set<QuadKey<LonLat>> {
    val cellRect = cellKey.computeRect(mapProjection.mapRect)
    val geoRect = ProjectionUtil.transformBBox(cellRect, mapProjection::invert)
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
