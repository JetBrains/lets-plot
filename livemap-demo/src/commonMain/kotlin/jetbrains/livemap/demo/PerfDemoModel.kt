/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.api.LiveMapBuilder
import jetbrains.livemap.api.layers
import jetbrains.livemap.api.point
import jetbrains.livemap.api.points
import jetbrains.livemap.model.coord

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
                                lat = -80.0 +  it % h * yDelta)
                        }
                    }
                }
            }
        }
    }

}