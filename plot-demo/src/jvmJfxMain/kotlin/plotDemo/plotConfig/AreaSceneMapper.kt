/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.presentation.Style.JFX_PLOT_STYLESHEET
import jetbrains.datalore.plotDemo.model.plotConfig.Area
import jetbrains.datalore.vis.demoUtils.jfx.SceneMapperDemoFactory

object AreaSceneMapper {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Area()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotConfigDemoUtil.show(
                "Area plot",
                plotSpecList,
                SceneMapperDemoFactory(JFX_PLOT_STYLESHEET),
                DoubleVector(600.0, 600.0)
            )
        }
    }
}
