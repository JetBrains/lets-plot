/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.geom

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plotDemo.model.geom.PathDemo
import jetbrains.datalore.vis.demoUtils.SceneMapperDemoFrame

class PathDemoSceneMapper : PathDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        SceneMapperDemoFrame.showSvg(svgRoots, listOf(Style.JFX_PLOT_STYLESHEET), demoComponentSize, "Path geom")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PathDemoSceneMapper().show()
        }
    }
}
