/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.common.component

import demo.livemap.common.Cities.GERMANY
import demo.livemap.model.Cities.POLAND
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.livemap.api.*
import org.jetbrains.letsPlot.livemap.config.DevParams
import org.jetbrains.letsPlot.livemap.config.DevParams.Companion.DEBUG_GRID
import org.jetbrains.letsPlot.livemap.config.DevParams.Companion.MICRO_TASK_EXECUTOR
import org.jetbrains.letsPlot.livemap.config.DevParams.Companion.PERF_STATS
import org.jetbrains.letsPlot.livemap.core.Projections

class FragmentDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {
    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            devParams = DevParams(
                    mapOf(PERF_STATS.key to true,
                        DEBUG_GRID.key to true,
                        MICRO_TASK_EXECUTOR.key to DevParams.MicroTaskExecutor.UI_THREAD.name
                        )
                )

            projection = Projections.conicEqualArea()

            geocodingService = Services.devGeocodingService()
            layers {
                polygons {
                    polygon {
                        fillColor = Color.PACIFIC_BLUE
                        strokeColor = Color.RED
                        strokeWidth = 1.0
                        sizeScalingRange = -2..Int.MAX_VALUE
                        alphaScalingEnabled = true
                        geoObject = GeoObject(
                            id = GERMANY.id,
                            centroid = GERMANY.centroid,
                            bbox = GERMANY.bbox,
                            position = GERMANY.position
                        )
                    }
                    polygon {
                        fillColor = Color.DARK_MAGENTA
                        strokeColor = Color.BLACK
                        strokeWidth = 1.0
                        sizeScalingRange = -2..Int.MAX_VALUE
                        alphaScalingEnabled = true
                        geoObject = GeoObject(
                            id = POLAND.id,
                            centroid = POLAND.centroid,
                            bbox = POLAND.bbox,
                            position = POLAND.position
                        )
                    }
                }
            }
        }
    }

}