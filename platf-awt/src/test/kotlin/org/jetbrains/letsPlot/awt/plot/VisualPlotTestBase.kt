/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.AwtBitmapIO
import demoAndTestShared.AwtTestCanvasProvider
import demoAndTestShared.ImageComparer
import kotlinx.coroutines.runBlocking
import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.awt.canvas.FontManager
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import javax.imageio.ImageIO

open class VisualPlotTestBase {

    private fun createImageComparer(): ImageComparer {
        return ImageComparer(
            tol = 5,
            canvasProvider = AwtTestCanvasProvider(),
            bitmapIO = AwtBitmapIO,
            expectedDir = System.getProperty("user.dir") + "/src/test/resources/expected-images/",
            outDir = System.getProperty("user.dir") + "/build/reports/"
        )
    }

    protected val imageComparer by lazy { createImageComparer() }

    fun assertPlot(
        expectedFileName: String,
        plotSpec: MutableMap<String, Any>,
        width: Number? = null,
        height: Number? = null,
        unit: SizeUnit? = null,
        dpi: Number? = null,
        scale: Number? = null,
        fontManager: FontManager = NotoFontManager.INSTANCE
    ) {
        val plotSize = if (width != null && height != null) DoubleVector(width, height) else null

        val imageData = runBlocking {
            PlotImageExport.buildImageFromRawSpecsInternal(
                plotSpec = plotSpec,
                format = PlotImageExport.Format.PNG,
                scalingFactor = scale ?: 1.0,
                targetDPI = dpi,
                plotSize = plotSize,
                unit = unit,
                fontManager = fontManager
            )
        }
        val image = ImageIO.read(imageData.bytes.inputStream())
        val bitmap = BitmapUtil.fromBufferedImage(image)

        imageComparer.assertBitmapEquals(expectedFileName, bitmap)
    }
}
