/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.swing.plotConfig

import demo.common.utils.swing.PlotSpecsDemoWindowSwing
import demo.plot.common.model.plotConfig.ImageWithScaleLimits

fun main() {
    with(ImageWithScaleLimits()) {
        PlotSpecsDemoWindowSwing(
            "image_geom",
            plotSpecList()
        ).open()
    }
}
