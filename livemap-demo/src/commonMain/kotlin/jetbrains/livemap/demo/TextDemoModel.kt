/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.api.*
import jetbrains.livemap.model.coord

class TextDemoModel(dimension: DoubleVector): DemoModelBase(dimension) {
    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            layers {

                texts {
                    text {
                        label = "--------0-->"
                        coord(0.0, 0.0)
                        fillColor = Color.GREEN
                        size = 25.0
                        angle = 0.0
                        vjust = 0.5
                    }

                    text {
                        label = "------180-->"
                        coord(0.0, 0.0)
                        fillColor = Color.BLUE
                        size = 25.0
                        angle = 180.0
                        vjust = 0.5
                    }

                    text {
                        label = "-------60-->"
                        coord(0.0, 0.0)
                        fillColor = Color.PINK
                        size = 25.0
                        angle = 60.0
                        vjust = 0.5
                    }

                    text {
                        label = "------300-->"
                        coord(0.0, 0.0)
                        fillColor = Color.RED
                        size = 25.0
                        angle = -60.0
                        vjust = 0.5
                    }

                    text {
                        label = "------120-->"
                        coord(0.0, 0.0)
                        fillColor = Color.CYAN
                        size = 25.0
                        angle = 120.0
                        vjust = 0.5
                    }

                    text {
                        label = "------210-->"
                        coord(0.0, 0.0)
                        fillColor = Color.DARK_MAGENTA
                        size = 25.0
                        angle = -120.0
                        vjust = 0.5
                    }
                }
            }
        }
    }
}