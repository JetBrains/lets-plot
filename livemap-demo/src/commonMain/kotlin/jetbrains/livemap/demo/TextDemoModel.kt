/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.api.*

class TextDemoModel(dimension: DoubleVector): DemoModelBase(dimension) {
    override fun createLiveMapSpec(): LiveMapBuilder {
        val coord1: LonLatPoint = explicitVec(0.0, 0.0)
        val coord2: LonLatPoint = explicitVec(25.0, 0.0)

        return basicLiveMap {
            layers {
                texts {
                    getTexts(coord1).map(::text)
                    getTexts(coord2).map(::label)
                }
                points(getPoint(coord1))
                points(getPoint(coord2))
            }
        }
    }

    private fun getPoint(coord: LonLatPoint): Points.() -> Unit = {
        point {
            point = coord
            radius = 4.0
            fillColor = Color.WHITE
            shape = 21
        }
    }

    private fun getTexts(coord: LonLatPoint) = listOf<TextBuilder.() -> Unit>(
        {
            label = "--------0-->"
            point = coord
            fillColor = Color.GREEN
            size = 25.0
            angle = 0.0
            vjust = 0.5
        },
        {
            label = "------180-->"
            point = coord
            fillColor = Color.BLUE
            size = 25.0
            angle = 180.0
            vjust = 0.5
        },
        {
            label = "-------60-->"
            point = coord
            fillColor = Color.PINK
            size = 25.0
            angle = 60.0
            vjust = 0.5
        },
        {
            label = "------300-->"
            point = coord
            fillColor = Color.RED
            size = 25.0
            angle = -60.0
            vjust = 0.5
        },
        {
            label = "------120-->"
            point = coord
            fillColor = Color.CYAN
            size = 25.0
            angle = 120.0
            vjust = 0.5
        },
        {
            label = "------210-->"
            point = coord
            fillColor = Color.DARK_MAGENTA
            size = 25.0
            angle = -120.0
            vjust = 0.5
        }
    )
}