/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.cells

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

class CellStateComponent : EcsComponent {
    var visibleCells: Set<CellKey> = HashSet()
    var requestCells: Set<CellKey> = HashSet()
    var cellsToRemove: Set<CellKey> = HashSet()

    val quadsRefCounter: HashMap<QuadKey<LonLat>, Int> = HashMap()
    var quadsToAdd: Set<QuadKey<LonLat>> = HashSet()
    var quadsToRemove: Set<QuadKey<LonLat>> = HashSet()

    val visibleQuads: Set<QuadKey<LonLat>>
        get() = quadsRefCounter.keys

    fun update(newVisibleCells: Set<CellKey>) {
        val existingCells = visibleCells

        visibleCells = newVisibleCells
        requestCells = visibleCells - existingCells
        cellsToRemove = existingCells - visibleCells
    }
}

class CellComponent(val cellKey: CellKey) : EcsComponent

class CellLayerComponent(val layerKind: CellLayerKind) : EcsComponent

class DebugCellLayerComponent : EcsComponent

enum class CellLayerKind constructor(private val myValue: String) {
    WORLD("world"),
    LABEL("label"),
    DEBUG("debug"),
    RASTER("raster_tile");

    override fun toString(): String {
        return myValue
    }
}

class KindComponent(val layerKind: CellLayerKind) : EcsComponent
