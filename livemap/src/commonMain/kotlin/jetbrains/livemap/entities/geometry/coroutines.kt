package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.Polygon
import jetbrains.datalore.base.projectionGeometry.Ring
import jetbrains.datalore.base.projectionGeometry.Vec
import kotlinx.coroutines.yield

object MultiPolygonTransformCoroutine {

    suspend fun <InT, OutT> transform(multiPolygon: MultiPolygon<InT>, f: (Vec<InT>, MutableCollection<Vec<OutT>>) -> Unit): MultiPolygon<OutT> {
        val newPolygons = ArrayList<Polygon<OutT>>()
        multiPolygon.forEach { polygon ->
            val newRings = ArrayList<Ring<OutT>>()
            polygon.forEach { ring ->
                val newPoints = ArrayList<Vec<OutT>>()
                ring.forEach { p ->
                    f(p, newPoints)
                }
                yield()
                newRings.add(Ring(newPoints))
            }
            newPolygons.add(Polygon(newRings))
        }

        return MultiPolygon(newPolygons)
    }
}