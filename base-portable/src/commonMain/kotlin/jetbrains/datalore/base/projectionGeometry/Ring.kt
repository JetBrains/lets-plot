package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.geometry.DoubleVector

class Ring(points: List<DoubleVector>) : AbstractGeometryList<DoubleVector>(points) {
    companion object {
        fun create(vararg points: DoubleVector): Ring {
            return Ring(arrayListOf(*points))
        }
    }
}
