/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotImage

import jetbrains.datalore.plot.PlotImageExport
import jetbrains.datalore.plotDemo.model.plotConfig.Raster

@Suppress("ClassName")
object Raster_PNG_Resolutions {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Raster()) {
            (PlotImageDemoUtil.show(
                "Raster plot",
                plotSpecList().first(),
                scalingFactors = listOf(1.0, 2.0, 4.0),
                targetDPIs = listOf(72, 144, 288),
                formats = MutableList(3) { PlotImageExport.Format.PNG }
            ))
        }
    }
}