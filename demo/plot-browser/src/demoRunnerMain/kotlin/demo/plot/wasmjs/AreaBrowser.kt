/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.wasmjs

import demo.plot.common.model.plotConfig.Area

fun main(args: Array<String>) {
    with(Area()) {
        (PlotConfigWasmJsDemoUtil.show(
            "Area plot",
            plotSpecList(),
        ))
    }
}
