/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.api.*
import jetbrains.livemap.model.coord

class BarsDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {

    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            layers {
                bars {
                    bar {
                        indices = listOf(0, 1, 2)
                        mapId = "Texas"

                        radius = 50.0
                        values = listOf(3000.0, 1500.0, 2000.0)
                        colors = listOf(Color.RED, Color.ORANGE, Color.YELLOW)
                    }

                    bar {
                        coord(-93.0, 42.0)

                        indices = listOf(3, 4, 5)

                        radius = 50.0
                        values = listOf(1500.0, 1000.0, 2500.0)
                        colors = listOf(Color.RED, Color.ORANGE, Color.YELLOW)
                    }
                }
            }
        }
    }
}