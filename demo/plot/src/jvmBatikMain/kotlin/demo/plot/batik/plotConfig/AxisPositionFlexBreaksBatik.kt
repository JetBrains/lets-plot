/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import demo.plot.common.model.plotConfig.AxisPositionFlexBreaks
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(AxisPositionFlexBreaks()) {
        PlotSpecsDemoWindowBatik(
            "Axis Position",
            plotSpecList()
        ).open()
    }
}
