/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.api.*
import jetbrains.livemap.projections.LonLatPoint

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

        return basicLiveMap {
            layers {
                paths {
                    path {
                        geometry(path1, isGeodesic = true)

                        strokeColor = Color.DARK_GREEN
                        strokeWidth = 6.0
                        lineDash = listOf(16.0, 20.0, 1.0, 20.0)
                    }

                    path {
                        geometry(path2, isGeodesic = true)

                        strokeColor = Color.BLUE
                        strokeWidth = 6.0
                        animation = 2
                    }
                }
            }
        }
    }
}