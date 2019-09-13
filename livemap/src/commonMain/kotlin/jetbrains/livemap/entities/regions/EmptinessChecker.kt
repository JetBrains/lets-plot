package jetbrains.livemap.entities.regions

import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.datalore.base.projectionGeometry.GeoUtils.getQuadKeyRect
import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.datalore.base.projectionGeometry.intersects

interface EmptinessChecker {
    fun test(regionId: String, quadKey: QuadKey): Boolean

    class DummyEmptinessChecker : EmptinessChecker {
        override fun test(regionId: String, quadKey: QuadKey): Boolean {
            return false
        }
    }

    class BBoxEmptinessChecker(private val regionBBoxes: Map<String, GeoRectangle>) : EmptinessChecker {

        override fun test(regionId: String, quadKey: QuadKey): Boolean {
            val quadKeyRect = getQuadKeyRect(quadKey)

            regionBBoxes[regionId]?.let {
                it.splitByAntiMeridian().forEach { bbox ->
                    if (bbox.intersects(quadKeyRect)) {
                        return false
                    }
                }
            }

            return true
        }
    }
}