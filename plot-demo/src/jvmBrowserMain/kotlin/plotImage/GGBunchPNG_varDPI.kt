/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotImage

import jetbrains.datalore.plot.PlotImageExport.Format
import jetbrains.datalore.plotDemo.model.plotConfig.GGBunch

@Suppress("ClassName")
object GGBunchPNG_varDPI {
    @JvmStatic
    fun main(args: Array<String>) {
        with(GGBunch()) {
            @Suppress("UNCHECKED_CAST")
            val plotSpecList = plotSpecList() as List<MutableMap<String, Any>>
            PlotImageDemoUtil.show(
                "GGBunch",
                plotSpecList.first(),
                scaleFactors = listOf(1.0, 2.0, 4.0),
                formats = MutableList(3) { Format.PNG }
            )
        }
    }
}
