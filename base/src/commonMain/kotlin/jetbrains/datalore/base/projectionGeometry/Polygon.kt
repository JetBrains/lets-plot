package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleRectangles

class Polygon(rings: List<Ring>) : AbstractGeometryList<Ring>(rings) {

    val limit: DoubleRectangle by lazy { DoubleRectangles.boundingBox(rings.asSequence().flatten().asIterable()) }

    companion object {
        fun create(vararg rings: Ring): Polygon {
            return Polygon(arrayListOf(*rings))
        }
    }
}
