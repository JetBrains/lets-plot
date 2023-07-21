/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotImage

import jetbrains.datalore.plot.PlotImageExport.Format
import jetbrains.datalore.plotDemo.model.plotConfig.Density2df

@Suppress("ClassName")
object Density2df_Formats {
    @JvmStatic
    fun main(args: Array<String>) {
        with(Density2df()) {
            @Suppress("UNCHECKED_CAST")
            (PlotImageDemoUtil.show(
                "Density2df plot",
                plotSpecList().first(),
                scalingFactors = listOf(2.0, 2.0, 2.0),
                targetDPIs = listOf(144, 144, 144),
                formats = listOf(Format.PNG, Format.TIFF, Format.JPEG())
            ))
        }
    }
}
