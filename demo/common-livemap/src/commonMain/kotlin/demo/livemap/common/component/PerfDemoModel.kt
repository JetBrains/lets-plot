/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.common.component

import demo.livemap.common.coord
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.livemap.api.LiveMapBuilder
import org.jetbrains.letsPlot.livemap.api.layers
import org.jetbrains.letsPlot.livemap.api.point
import org.jetbrains.letsPlot.livemap.api.points

class PerfDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {

    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            zoom = 1

            layers {

                points {
                    val w = 120
                    val h = 120
                    val xDelta = 360.0 / w
                    val yDelta = 160.0 / h
                    (0..w * h).forEach {
                        point {
                            shape = 21
                            strokeColor = Color.WHITE
                            coord(
                                lon = -180.0 + it / w * xDelta,
                                lat = -80.0 + it % h * yDelta
                            )
                        }
                    }
                }
            }
        }
    }

}