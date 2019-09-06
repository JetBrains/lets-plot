package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.geometry.DoubleRectangle

class MultiPolygon(polygons: List<Polygon>) : AbstractGeometryList<Polygon>(polygons) {
    constructor(polygon: Polygon) : this(listOf(polygon))

    val limits: List<DoubleRectangle> by lazy { map { it.limit } }

    companion object {
        fun create(vararg polygons: Polygon): MultiPolygon {
            return MultiPolygon(arrayListOf(*polygons))
        }
    }
}
