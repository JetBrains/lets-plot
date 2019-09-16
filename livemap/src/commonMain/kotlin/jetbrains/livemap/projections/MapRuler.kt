package jetbrains.livemap.projections

import jetbrains.datalore.base.projectionGeometry.Rect

interface MapRuler<TypeT> {
    fun deltaX(x1: Double, x2: Double): Double
    fun deltaY(y1: Double, y2: Double): Double

    fun distanceX(x1: Double, x2: Double): Double
    fun distanceY(y1: Double, y2: Double): Double

    fun calculateBoundingBox(xyRects: List<Rect<TypeT>>): Rect<TypeT>
}