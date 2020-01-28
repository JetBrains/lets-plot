/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plotDemo.model.plotConfig.BarOverlaidPlot
import jetbrains.datalore.vis.demoUtils.SceneMapperDemoFactory

object BarOverlaidPlotSceneMapper {
    @JvmStatic
    fun main(args: Array<String>) {
        with(BarOverlaidPlot()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "Overlaid bars plot",
                plotSpecList,
                SceneMapperDemoFactory(Style.JFX_PLOT_STYLESHEET),
                demoComponentSize
            )
        }
    }
}