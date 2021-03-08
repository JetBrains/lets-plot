/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowJfx
import jetbrains.livemap.plotDemo.LiveMap


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