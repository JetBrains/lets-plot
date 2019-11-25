/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.regions

import jetbrains.datalore.base.projectionGeometry.intersects
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.base.spatial.computeRect

interface EmptinessChecker {
    fun test(regionId: String, quadKey: QuadKey<LonLat>): Boolean

    class DummyEmptinessChecker : EmptinessChecker {
        override fun test(regionId: String, quadKey: QuadKey<LonLat>): Boolean {
            return false
        }
    }

    class BBoxEmptinessChecker(private val regionBBoxes: Map<String, GeoRectangle>) : EmptinessChecker {

        override fun test(regionId: String, quadKey: QuadKey<LonLat>): Boolean {
            val quadKeyRect = quadKey.computeRect()

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