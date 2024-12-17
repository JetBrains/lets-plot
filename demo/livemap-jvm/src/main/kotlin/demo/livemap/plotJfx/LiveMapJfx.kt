/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.plotJfx

import demo.common.util.demoUtils.jfx.PlotSpecsDemoWindowJfx
import demo.livemap.plotDemo.LiveMap

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