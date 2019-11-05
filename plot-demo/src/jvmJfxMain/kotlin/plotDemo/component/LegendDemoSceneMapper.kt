/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plotDemo.model.component.LegendDemo
import jetbrains.datalore.vis.demoUtils.jfx.SceneMapperDemoFrame

class LegendDemoSceneMapper : LegendDemo() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            LegendDemoSceneMapper().show()
        }
    }

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        SceneMapperDemoFrame.showSvg(svgRoots, listOf(Style.JFX_PLOT_STYLESHEET), demoComponentSize, "Legend component")
    }
}
