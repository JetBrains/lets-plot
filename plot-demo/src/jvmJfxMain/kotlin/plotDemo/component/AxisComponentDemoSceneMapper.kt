/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.plot.builder.presentation.Style.JFX_PLOT_STYLESHEET
import jetbrains.datalore.plotDemo.model.component.AxisComponentDemo
import jetbrains.datalore.vis.demoUtils.jfx.SceneMapperDemoFrame

fun main() {
    with(AxisComponentDemo()) {
        val svgRoots = createSvgRoots()
        SceneMapperDemoFrame.showSvg(
            svgRoots,
            listOf(JFX_PLOT_STYLESHEET),
            demoComponentSize,
            "Axis component (JFX SVG mapper)"
        )
    }
}

