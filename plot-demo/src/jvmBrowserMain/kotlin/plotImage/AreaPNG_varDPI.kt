/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotImage

import jetbrains.datalore.plot.PlotImageExport
import jetbrains.datalore.plot.PlotImageExport.Format
import jetbrains.datalore.plotDemo.model.plotConfig.Area

@Suppress("ClassName")
object AreaPNG_varDPI {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Area()) {
            @Suppress("UNCHECKED_CAST")
            (PlotImageDemoUtil.show(
                "Area plot",
                plotSpecList().first() as MutableMap<String, Any>,
                scaleFactors = listOf(1.0, 2.0, 4.0),
                formats = MutableList(3) { Format.PNG}
            ))
        }
    }
}
