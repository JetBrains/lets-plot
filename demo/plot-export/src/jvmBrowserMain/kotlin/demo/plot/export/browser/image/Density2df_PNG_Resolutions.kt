/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.export.browser.image

import jetbrains.datalore.plot.PlotImageExport.Format
import demo.plot.common.model.plotConfig.Density2df

@Suppress("ClassName")
object Density2df_PNG_Resolutions {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Density2df()) {
            @Suppress("UNCHECKED_CAST")
            (PlotImageDemoUtil.show(
                "Density2df plot",
                plotSpecList().first(),
                scalingFactors = listOf(1.0, 2.0, 4.0),
                targetDPIs = listOf(72, 144, 288),
                formats = MutableList(3) { Format.PNG }
            ))
        }
    }
}
