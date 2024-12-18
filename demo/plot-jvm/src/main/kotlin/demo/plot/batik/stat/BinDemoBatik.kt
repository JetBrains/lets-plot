/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.stat

import demo.common.utils.batik.SvgViewerDemoWindowBatik
import demo.plot.shared.model.stat.BinDemo

fun main() {
    with(BinDemo()) {
        SvgViewerDemoWindowBatik(
            "Bin stat",
            createSvgRoots(createModels())
        ).open()
    }
}
