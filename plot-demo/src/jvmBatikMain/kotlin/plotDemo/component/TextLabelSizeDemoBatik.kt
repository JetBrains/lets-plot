/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.plotDemo.model.component.TextLabelSizeDemo
import jetbrains.datalore.vis.demoUtils.SvgViewerDemoWindowBatik

fun main() {
    with(TextLabelSizeDemo()) {
        SvgViewerDemoWindowBatik(
            "Text label size and style",
            createSvgRoots(listOf(createModel()))
        ).open()
    }
}