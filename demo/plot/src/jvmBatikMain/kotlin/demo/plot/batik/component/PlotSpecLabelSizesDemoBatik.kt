/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import demo.plot.shared.model.component.PlotSpecLabelSizesDemo
import demo.common.batik.demoUtils.SvgViewerDemoWindowBatik

fun main() {
    with(PlotSpecLabelSizesDemo()) {
        SvgViewerDemoWindowBatik(
            "Label sizes",
            createSvgRoots(createModels()),
            maxCol = 3
        ).open()
    }
}