/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plotDemo.plotConfig.PlotConfigDemoUtil
import jetbrains.datalore.vis.demoUtils.SceneMapperDemoFactory
import jetbrains.livemap.plotDemo.LiveMap

object LiveMapSceneMapper {
    @JvmStatic
    fun main(args: Array<String>) {
        with(LiveMap()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "Bar & Line plot",
                plotSpecList,
                SceneMapperDemoFactory(Style.JFX_PLOT_STYLESHEET),
                demoComponentSize
            )
        }
    }
}