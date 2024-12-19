/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.export.browser.image

import demo.plot.common.model.plotConfig.SpecialSymbols
import org.jetbrains.letsPlot.core.plot.export.PlotImageExport

object SpecialSymbols {
    @JvmStatic
    fun main(args: Array<String>) {
        with(SpecialSymbols()) {
            @Suppress("UNCHECKED_CAST")
            (PlotImageDemoUtil.show(
                "Special symbols",
                plotSpec(),
                scalingFactors = listOf(2.0, 2.0, 2.0),
                targetDPIs = listOf(144, 144, 144),
                formats = listOf(PlotImageExport.Format.PNG, PlotImageExport.Format.TIFF, PlotImageExport.Format.JPEG())
            ))
        }
    }
}
