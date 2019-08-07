package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

interface ViewProjectionHelper {
    fun normalizeX(x: Double): Double
    fun normalizeY(y: Double): Double

    fun getOrigins(objRect: DoubleRectangle, viewRect: DoubleRectangle): List<DoubleVector>
    fun getCells(viewRect: DoubleRectangle, cellLevel: Int): Set<CellKey>
}