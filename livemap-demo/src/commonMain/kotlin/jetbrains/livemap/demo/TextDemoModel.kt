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
                texts(getTexts(coord1, isLabel = false))
                texts(getTexts(coord2, isLabel = true))
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

    private fun getTexts(coord: LonLatPoint, isLabel: Boolean): Texts.() -> Unit = {
        text {
            label = "--------0-->"
            point = coord
            Color.GREEN.let {
                if (isLabel) fillColor = it else strokeColor = it
            }
            size = 25.0
            angle = 0.0
            vjust = 0.5
            drawBorder = isLabel
        }
        text {
            label = "------180-->"
            point = coord
            Color.BLUE.let {
                if (isLabel) fillColor = it else strokeColor = it
            }
            size = 25.0
            angle = 180.0
            vjust = 0.5
            drawBorder = isLabel
        }
        text {
            label = "-------60-->"
            point = coord
            Color.PINK.let {
                if (isLabel) fillColor = it else strokeColor = it
            }
            size = 25.0
            angle = 60.0
            vjust = 0.5
            drawBorder = isLabel
        }
        text {
            label = "------300-->"
            point = coord
            Color.RED.let {
                if (isLabel) fillColor = it else strokeColor = it
            }
            size = 25.0
            angle = -60.0
            vjust = 0.5
            drawBorder = isLabel
        }
        text {
            label = "------120-->"
            point = coord
            Color.CYAN.let {
                if (isLabel) fillColor = it else strokeColor = it
            }
            size = 25.0
            angle = 120.0
            vjust = 0.5
            drawBorder = isLabel
        }
        text {
            label = "------210-->"
            point = coord
            Color.DARK_MAGENTA.let {
                if (isLabel) fillColor = it else strokeColor = it
            }
            size = 25.0
            angle = -120.0
            vjust = 0.5
            drawBorder = isLabel
        }
    }
}