/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.export.browser.image

import demo.plot.common.model.plotConfig.Density2df
import org.jetbrains.letsPlot.core.plot.export.PlotImageExport.Format.*

fun main(args: Array<String>) {
    with(Density2df()) {
        @Suppress("UNCHECKED_CAST")
        (PlotImageDemoUtil.show(
            "Density2df plot",
            plotSpecList().first(),
            scalingFactors = listOf(2.0, 2.0, 2.0),
            targetDPIs = listOf(144, 144, 144),
            formats = listOf(PNG, TIFF, JPEG())
        ))
    }
}
