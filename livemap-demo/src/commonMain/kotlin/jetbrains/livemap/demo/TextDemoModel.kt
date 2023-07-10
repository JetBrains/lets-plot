/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.api.*

class TextDemoModel(dimension: DoubleVector): DemoModelBase(dimension) {
    override fun createLiveMapSpec(): LiveMapBuilder {
        val coord1: LonLatPoint = explicitVec(0.0, 0.0)
        val coord2: LonLatPoint = explicitVec(25.0, 0.0)
        val coord3: LonLatPoint = explicitVec(0.0, -10.0)
        val coord4: LonLatPoint = explicitVec(25.0, -10.0)

        return basicLiveMap {
            layers {
                texts(getTexts(coord1, withBorder = false))
                texts(getTexts(coord2, withBorder = true))
                points(getPoint(coord1))
                points(getPoint(coord2))
                texts(multiline(coord3, withBorder = false))
                points(getPoint(coord3))
                texts(multiline(coord4, withBorder = true))
                points(getPoint(coord4))

            }
        }
    }

    private fun getPoint(coord: LonLatPoint): PointLayerBuilder.() -> Unit = {
        point {
            point = coord
            radius = 4.0
            fillColor = Color.WHITE
            shape = 21
        }
    }

    private fun getTexts(coord: LonLatPoint, withBorder: Boolean): TextLayerBuilder.() -> Unit = {
        text {
            label = "--------0-->"
            point = coord
            Color.GREEN.let {
                if (withBorder) fillColor = it else strokeColor = it
            }
            size = 25.0
            angle = 0.0
            vjust = 0.5
            drawBorder = withBorder
        }
        text {
            label = "------180-->"
            point = coord
            Color.BLUE.let {
                if (withBorder) fillColor = it else strokeColor = it
            }
            size = 25.0
            angle = 180.0
            vjust = 0.5
            drawBorder = withBorder
        }
        text {
            label = "-------60-->"
            point = coord
            Color.PINK.let {
                if (withBorder) fillColor = it else strokeColor = it
            }
            size = 25.0
            angle = 60.0
            vjust = 0.5
            drawBorder = withBorder
        }
        text {
            label = "------300-->"
            point = coord
            Color.RED.let {
                if (withBorder) fillColor = it else strokeColor = it
            }
            size = 25.0
            angle = -60.0
            vjust = 0.5
            drawBorder = withBorder
        }
        text {
            label = "------120-->"
            point = coord
            Color.CYAN.let {
                if (withBorder) fillColor = it else strokeColor = it
            }
            size = 25.0
            angle = 120.0
            vjust = 0.5
            drawBorder = withBorder
        }
        text {
            label = "------210-->"
            point = coord
            Color.DARK_MAGENTA.let {
                if (withBorder) fillColor = it else strokeColor = it
            }
            size = 25.0
            angle = -120.0
            vjust = 0.5
            drawBorder = withBorder
        }
    }

    private fun multiline(coord: LonLatPoint, withBorder: Boolean): TextLayerBuilder.() -> Unit = {
        text {
            label = "first line\nthe second\n3"
            point = coord
            size = 20.0
            angle = 20.0
            vjust = 1.0
            hjust = 0.5
            labelPadding = 1.0
            lineheight = 1.5
            drawBorder = withBorder
        }
    }
}