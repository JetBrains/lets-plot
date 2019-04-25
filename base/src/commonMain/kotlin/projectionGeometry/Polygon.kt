package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleRectangles

class Polygon(rings: List<Ring>) : AbstractGeometryList<Ring>(rings) {

    private var myLimit: DoubleRectangle? = null

    val limit: DoubleRectangle
        get() {
            if (myLimit == null) {
                myLimit = DoubleRectangles.boundingBox(this.flatten())
            }
            return myLimit!!
        }

    companion object {
        fun create(vararg rings: Ring): Polygon {
            return Polygon(arrayListOf(*rings))
        }
    }
}
