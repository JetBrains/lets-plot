/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.component

import demo.common.utils.batik.SvgViewerDemoWindowBatik
import demo.plot.shared.model.component.RichTextDemo

fun main() {
    with(RichTextDemo()) {
        SvgViewerDemoWindowBatik(
            "Rich text",
            createSvgRoots(listOf(createModel()))
        ).open()
    }
}