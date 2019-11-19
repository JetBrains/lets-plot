/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.scale

import jetbrains.datalore.plot.builder.presentation.Style.JFX_PLOT_STYLESHEET
import jetbrains.datalore.plotDemo.model.scale.DateTimeScaleX
import jetbrains.datalore.plotDemo.plotConfig.PlotConfigDemoUtil
import jetbrains.datalore.vis.demoUtils.jfx.SceneMapperDemoFactory

object DateTimeScaleXSceneMapper {
    @JvmStatic
    fun main(args: Array<String>) {
        with(DateTimeScaleX()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "datetime scale",
                plotSpecList,
                SceneMapperDemoFactory(JFX_PLOT_STYLESHEET),
                demoComponentSize
            )
        }
    }
}

