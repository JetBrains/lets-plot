/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotSvg

import demo.plot.common.model.plotConfig.Density2df

object Density2dfSvg {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Density2df()) {
            @Suppress("UNCHECKED_CAST")
            (PlotSvgDemoUtil.show(
                "Density2df plot",
                plotSpecList(),
//                demoComponentSize
            ))
        }
    }
}
