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