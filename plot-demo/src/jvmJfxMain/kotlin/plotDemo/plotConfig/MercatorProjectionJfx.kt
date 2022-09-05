/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.MercatorProjection
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowJfx
import java.awt.Dimension

fun main() {
    with(MercatorProjection()) {
        PlotSpecsDemoWindowJfx(
            "Mercator projection",
            plotSpecList(),
            maxCol = 2,
            plotSize = Dimension(400, 300)
        ).open()
    }
}
