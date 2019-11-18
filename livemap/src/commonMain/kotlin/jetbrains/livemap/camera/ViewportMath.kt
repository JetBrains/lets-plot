/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.camera

import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.livemap.projections.WorldPoint
import jetbrains.livemap.projections.WorldRectangle
import jetbrains.livemap.tiles.CellKey

interface ViewportMath {
    fun normalizeX(x: Double): Double
    fun normalizeY(y: Double): Double

    fun <T> normalize(v: Vec<T>): Vec<T> = explicitVec<T>(normalizeX(v.x), normalizeY(v.y))

    fun getOrigins(objRect: WorldRectangle, viewRect: WorldRectangle): List<WorldPoint>
    fun getCells(viewRect: WorldRectangle, cellLevel: Int): Set<CellKey>
}