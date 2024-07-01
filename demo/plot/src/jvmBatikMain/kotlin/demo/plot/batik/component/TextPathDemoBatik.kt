/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.component

import demo.common.batik.demoUtils.SvgViewerDemoWindowBatik
import demo.plot.shared.model.component.TextPathDemo

fun main() {
    with(TextPathDemo()) {
        SvgViewerDemoWindowBatik(
            "Text path demo",
            createSvgRoots(listOf(createModel()))
        ).open()
    }
}