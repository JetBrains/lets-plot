/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotAssembler

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plotDemo.model.plotAssembler.BarPlotDemo
import jetbrains.datalore.vis.demoUtils.jfx.SceneMapperDemoFrame

class BarPlotDemoSceneMapper : BarPlotDemo() {

    private fun show() {
        val plots = createPlots()
        val svgRoots = createSvgRootsFromPlots(plots)
        SceneMapperDemoFrame.showSvg(svgRoots, listOf(Style.JFX_PLOT_STYLESHEET), demoComponentSize, "Bar plot")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BarPlotDemoSceneMapper().show()
        }
    }
}
