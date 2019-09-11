package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle

interface ViewProjectionHelper {
    fun normalizeX(x: Double): Double
    fun normalizeY(y: Double): Double

    fun getOrigins(objRect: DoubleRectangle, viewRect: DoubleRectangle): List<WorldPoint>
    fun getCells(viewRect: DoubleRectangle, cellLevel: Int): Set<CellKey>
}