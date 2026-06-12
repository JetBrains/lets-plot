/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.awt.canvas.FontManager
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.raster.view.PlotCanvasDrawable
import org.jetbrains.letsPlot.visualtesting.plot.PlotVisualTestBase
import javax.imageio.ImageIO

@Suppress("UNCHECKED_CAST")
internal fun PlotVisualTestBase.createPlotFromSpec(spec: MutableMap<*, *>): PlotCanvasDrawable {
    return createPlot(spec as MutableMap<String, Any?>)
}

@Suppress("UNCHECKED_CAST")
internal fun PlotVisualTestBase.assertExportedPlot(
    expectedFileName: String,
    plotSpec: MutableMap<*, *>,
    width: Number? = null,
    height: Number? = null,
    unit: SizeUnit? = null,
    dpi: Number? = null,
    scale: Number? = null,
    fontManager: FontManager = NotoFontManager.INSTANCE
) {
    val exportedSpec = plotSpec as MutableMap<String, Any>
    val plotSize = if (width != null && height != null) DoubleVector(width, height) else null

    val imageData = PlotImageExport.buildImageFromRawSpecs(
        plotSpec = exportedSpec,
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
