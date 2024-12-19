/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.common.component

import demo.livemap.common.Cities
import demo.livemap.common.coord
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.livemap.api.LiveMapBuilder
import org.jetbrains.letsPlot.livemap.api.layers
import org.jetbrains.letsPlot.livemap.api.pie
import org.jetbrains.letsPlot.livemap.api.pies
import org.jetbrains.letsPlot.livemap.chart.donut.StrokeSide

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
                        strokeSide = StrokeSide.BOTH
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