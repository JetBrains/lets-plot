/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.common.component

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.LonLatPoint
import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.livemap.api.*

class LinesDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {

    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            val points = listOf<LonLatPoint>(
                explicitVec(-110.0, 55.0),
                explicitVec(-100.0, 45.0),
                explicitVec(-90.0, 35.0),
                explicitVec(-80.0, 25.0)
            )

            layers {
                hLines {
                    points.forEach {
                        line {
                            point = it
                            strokeColor = Color.RED
                            strokeWidth = 1.0
                            lineDash = listOf(4.0, 4.0)
                        }
                    }
                }
                vLines {
                    points.forEach {
                        line {
                            point = it
                            strokeColor = Color.DARK_GREEN
                            strokeWidth = 1.0
                            lineDash = listOf(6.0, 6.0, 1.0, 6.0)
                        }
                    }
                }
            }
        }
    }
}