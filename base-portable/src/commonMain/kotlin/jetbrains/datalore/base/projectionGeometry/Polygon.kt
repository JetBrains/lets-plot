package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleRectangles

class Polygon(rings: List<Ring>) : AbstractGeometryList<Ring>(rings) {
    constructor(ring: Ring) : this(listOf(ring))

    val limit: DoubleRectangle by lazy { DoubleRectangles.boundingBox(rings.asSequence().flatten().asIterable()) }
}
