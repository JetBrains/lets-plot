/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.plotConfig

import demo.common.util.demoUtils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.ImageWithScaleLimits

fun main() {
    with(ImageWithScaleLimits()) {
        PlotSpecsDemoWindowBatik(
            "image_geom",
            plotSpecList()
        ).open()
    }
}
