/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.plotDemo.model.component.TextJustificationDemo
import jetbrains.datalore.vis.demoUtils.SvgViewerDemoWindowBatik

fun main() {
    with(TextJustificationDemo()) {
        SvgViewerDemoWindowBatik(
            "Text justifications (Batik)",
            createSvgRoots(listOf(createModel()))
        ).open()
    }
}