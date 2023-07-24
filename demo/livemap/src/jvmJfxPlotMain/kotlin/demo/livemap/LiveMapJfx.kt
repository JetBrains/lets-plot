/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap

import demo.livemap.plotDemo.LiveMap
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowJfx


object LiveMapJfx {
    @JvmStatic
    fun main(args: Array<String>) {
        with(LiveMap()) {
            PlotSpecsDemoWindowJfx(
                "LiveMap (JavaFX)",
                plotSpecList(),
                maxCol = 2
            ).open()
        }
    }
}