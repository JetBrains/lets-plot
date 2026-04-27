/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.awt.canvas.FontManager
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.visualtesting.AwtBitmapIO
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import javax.imageio.ImageIO

open class VisualPlotTestBase(
    private val expectedImagesSubdir: String = ""
) {

    private fun createImageComparer(): ImageComparer {
        val canvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
        val awtBitmapIO = AwtBitmapIO(subdir = expectedImagesSubdir)
        return ImageComparer(canvasPeer, awtBitmapIO, silent = true)
    }

    protected val imageComparer by lazy { createImageComparer() }

    fun assertPlot(
        expectedFileName: String,
        plotSpec: MutableMap<*, *>,
        width: Number? = null,
        height: Number? = null,
        unit: SizeUnit? = null,
        dpi: Number? = null,
        scale: Number? = null,
        fontManager: FontManager = NotoFontManager.INSTANCE
    ) {
        @Suppress("UNCHECKED_CAST")
        val plotSpec = plotSpec as MutableMap<String, Any>

        val plotSize = if (width != null && height != null) DoubleVector(width, height) else null

        val imageData = PlotImageExport.buildImageFromRawSpecs(
                plotSpec = plotSpec,
                format = PlotImageExport.Format.PNG,
                scalingFactor = scale ?: 1.0,
                targetDPI = dpi,
                plotSize = plotSize,
                unit = unit,
                fontManager = fontManager
            )

        val image = ImageIO.read(imageData.bytes.inputStream())
        val bitmap = BitmapUtil.fromBufferedImage(image)

        imageComparer.assertBitmapEquals(expectedFileName, bitmap)
    }
}
