/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.export.browser.image

import jetbrains.datalore.plot.PlotImageExport.Format
import demo.plot.common.model.plotConfig.Area

@Suppress("ClassName")
object Area_PNG_Resolutions {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Area()) {
            @Suppress("UNCHECKED_CAST")
            (PlotImageDemoUtil.show(
                "Area plot",
                plotSpecList().first(),
                scalingFactors = listOf(1.0, 2.0, 4.0),
                targetDPIs = listOf(72, 144, 288),
                formats = MutableList(3) { Format.PNG }
            ))
        }
    }
}
