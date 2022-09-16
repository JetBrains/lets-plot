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
                    getTexts(coord1, isText = true).map(::text)
                    getTexts(coord2, isText = false).map(::label)
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

    private fun getTexts(coord: LonLatPoint, isText: Boolean) = listOf<TextBuilder.() -> Unit>(
        {
            label = "--------0-->"
            point = coord
            Color.GREEN.let {
                if (isText) strokeColor = it else fillColor = it
            }
            size = 25.0
            angle = 0.0
            vjust = 0.5
        },
        {
            label = "------180-->"
            point = coord
            Color.BLUE.let {
                if (isText) strokeColor = it else fillColor = it
            }
            size = 25.0
            angle = 180.0
            vjust = 0.5
        },
        {
            label = "-------60-->"
            point = coord
            Color.PINK.let {
                if (isText) strokeColor = it else fillColor = it
            }
            size = 25.0
            angle = 60.0
            vjust = 0.5
        },
        {
            label = "------300-->"
            point = coord
            Color.RED.let {
                if (isText) strokeColor = it else fillColor = it
            }
            size = 25.0
            angle = -60.0
            vjust = 0.5
        },
        {
            label = "------120-->"
            point = coord
            Color.CYAN.let {
                if (isText) strokeColor = it else fillColor = it
            }
            size = 25.0
            angle = 120.0
            vjust = 0.5
        },
        {
            label = "------210-->"
            point = coord
            Color.DARK_MAGENTA.let {
                if (isText) strokeColor = it else fillColor = it
            }
            size = 25.0
            angle = -120.0
            vjust = 0.5
        }
    )
}