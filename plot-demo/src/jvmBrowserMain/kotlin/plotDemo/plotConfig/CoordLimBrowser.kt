/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.CoordLim

object CoordLimBrowser {
    @JvmStatic
    fun main(args: Array<String>) {
        with(CoordLim()) {
            @Suppress("UNCHECKED_CAST")
            (PlotConfigDemoUtil.show(
                "coord_cartesian(x_lim=[2, 22])",
                plotSpecList() as List<MutableMap<String, Any>>,
                demoComponentSize
            ))
        }
    }}