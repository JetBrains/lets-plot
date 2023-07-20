/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
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
                        values = listOf(2.0, 5.0, 1.0)
                        fillColors = listOf(Color.LIGHT_GREEN, Color.LIGHT_PINK, Color.LIGHT_BLUE)
                        strokeColors = listOf(Color.DARK_GREEN, Color.PINK, Color.DARK_BLUE)
                        strokeWidths = List(3) { 4.0 }
                        strokeSide = "both"
                        spacerWidth = 4.0
                    }

                    pie {
                        indices = listOf(0, 1, 2, 3)
                        coord(Cities.NEW_YORK)
                        radius = 50.0
                        values = listOf(3.0, 1.0, 2.0, 4.0)
                        fillColors = listOf(Color.DARK_GREEN, Color.ORANGE, Color.DARK_MAGENTA, Color.DARK_BLUE)

                        strokeColors = List(4) { Color.WHITE }
                        strokeWidths = List(4) { 2.0 }

                        spacerColor = Color.WHITE
                        spacerWidth = 2.0

                        explodes = listOf(.0, .2, .0, .0)
                    }
                }
            }
        }
    }
}