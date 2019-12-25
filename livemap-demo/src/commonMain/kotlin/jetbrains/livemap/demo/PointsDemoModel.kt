/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.api.*
import jetbrains.livemap.model.Cities.BOSTON
import jetbrains.livemap.model.Cities.MOSCOW
import jetbrains.livemap.model.Cities.NEW_YORK
import jetbrains.livemap.model.Cities.SPB
import jetbrains.livemap.model.coord

class PointsDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {

    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            layers {
                points {

                    point {
                        coord(MOSCOW)
                        strokeColor = Color.RED
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

                    point {
                        mapId = "Texas"
                        shape = 21
                        fillColor = Color.DARK_MAGENTA
                    }
                }
            }
        }
    }
}


