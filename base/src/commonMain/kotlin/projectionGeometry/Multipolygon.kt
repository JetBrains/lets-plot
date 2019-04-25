package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.gcommon.collect.Lists
import jetbrains.datalore.base.geometry.DoubleRectangle

class Multipolygon(polygons: List<Polygon>) : AbstractGeometryList<Polygon>(polygons) {

    private var myLimits: List<DoubleRectangle>? = null

    val limits: List<DoubleRectangle>?
        get() {
            if (myLimits == null) {
                myLimits = getLimits(this)
            }
            return myLimits
        }

    companion object {
        private fun getLimits(polygons: List<Polygon>): List<DoubleRectangle> {
            return Lists.transform(polygons) { polygon -> polygon.limit }
        }

        fun create(vararg polygons: Polygon): Multipolygon {
            return Multipolygon(arrayListOf(*polygons))
        }
    }
}
