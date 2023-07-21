/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.plotDemo.model.component.PlotSpecLabelSizesDemo
import jetbrains.datalore.vis.demoUtils.SvgViewerDemoWindowJfx

fun main() {
    with(PlotSpecLabelSizesDemo()) {
        SvgViewerDemoWindowJfx(
            "Label sizes",
            createSvgRoots(createModels()),
            maxCol = 3
        ).open()
    }
}