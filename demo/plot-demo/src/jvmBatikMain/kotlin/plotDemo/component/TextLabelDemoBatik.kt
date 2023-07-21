/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.plotDemo.model.component.TextLabelDemo
import jetbrains.datalore.vis.demoUtils.SvgViewerDemoWindowBatik

fun main() {
    with(TextLabelDemo()) {
        SvgViewerDemoWindowBatik(
            "Text label anchor and rotation",
            createSvgRoots(listOf(createModel()))
        ).open()
    }
}