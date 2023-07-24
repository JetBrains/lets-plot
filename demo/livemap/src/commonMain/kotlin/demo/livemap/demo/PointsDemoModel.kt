/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.demo

import demo.livemap.model.Cities.BOSTON
import demo.livemap.model.Cities.MOSCOW
import demo.livemap.model.Cities.NEW_YORK
import demo.livemap.model.coord
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.livemap.api.LiveMapBuilder
import org.jetbrains.letsPlot.livemap.api.layers
import org.jetbrains.letsPlot.livemap.api.point
import org.jetbrains.letsPlot.livemap.api.points

class PointsDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {

    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            layers {
                points {

                    point {
                        coord(MOSCOW)
                        sizeScalingRange = -2..3
                        alphaScalingEnabled = true

                        shape = 21
                        strokeColor = Color.RED
                        fillColor = Color.LIGHT_GREEN
                    }
                    point {
                        coord(BOSTON)
                        strokeColor = Color.BLUE
                    }
                    point {
                        coord(NEW_YORK)
                        strokeColor = Color.GREEN
                    }

                    point {
                        coord(0.0, 0.0)

                        animation = 2
                        shape = 21
                        radius = 10.0
                        fillColor = Color.MAGENTA
                    }
                }
            }
        }
    }
}


