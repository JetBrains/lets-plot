/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.stat

import jetbrains.datalore.plotDemo.model.stat.BinDemo
import jetbrains.datalore.vis.demoUtils.SvgViewerDemoWindowBatik

fun main() {
    with(BinDemo()) {
        SvgViewerDemoWindowBatik(
            "Bin stat",
            createSvgRoots(createModels())
        ).open()
    }
}
