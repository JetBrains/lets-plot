/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.LonLatPoint
import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.livemap.api.*

class PathsDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {

    override fun createLiveMapSpec(): LiveMapBuilder {
        val path1 = listOf<LonLatPoint>(
            explicitVec(-121.904262, 34.685259),
            explicitVec(-113.994106, 29.628478),
            explicitVec(-80.156215, 48.877432),
            explicitVec(-102.304653, 48.992905)
        )

        val path2 = listOf<LonLatPoint>(
            explicitVec(-122.607387, 31.295074),
            explicitVec(-114.521450, 26.054413),
            explicitVec(-73.970668, 49.076602),
            explicitVec(-102.568325, 51.356414)
        )

        val path3 = listOf<LonLatPoint>(
            explicitVec(-102.5513756, 26.3281169),
            explicitVec(-82.9946306, 39.5602949),
            explicitVec(-75.2230467, 40.7465046)
        )

        val path4 = listOf<LonLatPoint>(
            explicitVec(-100.2052394, 32.2622384),
            explicitVec(-84.9196668, 31.9644662)
        )

        return basicLiveMap {
            layers {
                paths {
                    path {
                        points = path1
                        flat = false

                        strokeColor = Color.DARK_GREEN
                        strokeWidth = 6.0
                        lineDash = listOf(16.0, 20.0, 1.0, 20.0)
                    }

                    path {
                        points = path2
                        flat = false

                        strokeColor = Color.BLUE
                        strokeWidth = 6.0
                        animation = 2
                    }

                    path {
                        points = path3
                        flat = false

                        strokeColor = Color.RED
                        strokeWidth = 3.0
                        lineDash = listOf(5.0)
                        arrow(length = 20.0, ends = "both") // type = "open"
                    }
                    path {
                        points = path4
                        flat = false

                        strokeColor = Color.RED
                        strokeWidth = 3.0
                        arrow(ends = "both", type = "closed")
                    }
                }
            }
        }
    }
}