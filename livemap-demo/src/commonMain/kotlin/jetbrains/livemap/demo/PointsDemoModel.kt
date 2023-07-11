/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.livemap.api.LiveMapBuilder
import jetbrains.livemap.api.layers
import jetbrains.livemap.api.point
import jetbrains.livemap.api.points
import jetbrains.livemap.model.Cities.BOSTON
import jetbrains.livemap.model.Cities.MOSCOW
import jetbrains.livemap.model.Cities.NEW_YORK
import jetbrains.livemap.model.coord

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


