/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.SvgUID
import jetbrains.datalore.plot.testing.rawSpec_GGBunch
import jetbrains.datalore.plot.testing.rawSpec_SinglePlot
import org.junit.Before
import org.junit.Test
import java.awt.Desktop
import java.io.File

class PlotPngExportTest {
    @Before
    fun setUp() {
        SvgUID.setUpForTest()
    }

    @Test
    fun pngFromSinglePlot() {
        export(
            plotSpec = rawSpec_SinglePlot(),
            plotSize = DoubleVector(400.0, 300.0)
        )
    }

    @Test
    fun pngFromGGBunch() {
        export(
            plotSpec = rawSpec_GGBunch(),
            plotSize = DoubleVector(800.0, 400.0)
        )
    }

    private fun export(plotSpec: MutableMap<String, Any>, plotSize: DoubleVector?, outputPath: String? = null) {
        val image = PlotPngExport.buildPngImageFromRawSpecs(
            plotSpec = plotSpec,
            plotSize = plotSize
        )

        val file = when (outputPath) {
            null -> File.createTempFile("lets_plot_export", "png")
            else -> File(outputPath)
        }

        file.writeBytes(image)
        Desktop.getDesktop().open(file)
    }

}