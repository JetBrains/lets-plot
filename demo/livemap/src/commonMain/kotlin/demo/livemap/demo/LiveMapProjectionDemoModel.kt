/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.demo

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.livemap.api.LiveMapBuilder
import org.jetbrains.letsPlot.livemap.config.DevParams
import org.jetbrains.letsPlot.livemap.core.Projections

class LiveMapProjectionDemoModel(dimension: DoubleVector): DemoModelBase(dimension) {
    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            projection = Projections.azimuthalEqualArea()
            devParams = DevParams(mapOf(
                DevParams.PERF_STATS.key to true,
            ))
        }
    }
}