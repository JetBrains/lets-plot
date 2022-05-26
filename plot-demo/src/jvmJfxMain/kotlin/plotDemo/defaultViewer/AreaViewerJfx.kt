/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.defaultViewer

import jetbrains.datalore.plotDemo.model.plotConfig.Area
import jetbrains.datalore.vis.swing.jfx.PlotViewerWindowJfx

object AreaViewerJfx {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Area()) {
            PlotViewerWindowJfx(
                "Area plot",
                null,
                plotSpecList().first(),
//                    Dimension(900, 700),
                preserveAspectRatio = false
            ).open()
        }
    }
}
