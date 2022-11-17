/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.api.LiveMapBuilder
import jetbrains.livemap.api.layers
import jetbrains.livemap.api.pie
import jetbrains.livemap.api.pies
import jetbrains.livemap.model.Cities
import jetbrains.livemap.model.coord

class PiesDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {

    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            layers {
                pies {
                    pie {
                        indices = listOf(0, 1, 2)
                        coord(Cities.FRISCO)
                        radius = 50.0
                        values = listOf(-2.0, 5.0, 1.0)
                        colors = listOf(Color.DARK_GREEN, Color.ORANGE, Color.DARK_MAGENTA)
                        strokeColor = Color.WHITE
                        strokeWidth = 2.0
                        holeSize = 0.4
                    }

                    pie {
                        indices = listOf(0, 1, 2)
                        coord(Cities.NEW_YORK)
                        radius = 50.0
                        values = listOf(3.0, 1.0, 2.0)
                        colors = listOf(Color.DARK_GREEN, Color.ORANGE, Color.DARK_MAGENTA)
                        explodes = listOf(.0, .2, .0)
                    }
                }
            }
        }
    }
}