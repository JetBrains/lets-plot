/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.visualtesting.plot

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.raster.export.PlotRasterExport
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.TestSuit

internal abstract class PlotTestBase : TestSuit() {
    abstract val imageComparer: ImageComparer
    abstract val canvasPeer: CanvasPeer

    fun assertPlot(
        expectedFileName: String,
        plotSpec: MutableMap<String, Any?>,
        width: Number? = null,
        height: Number? = null,
        unit: SizeUnit? = null,
        dpi: Number? = null,
        scale: Number? = 1.0
    ) {
        @Suppress("UNCHECKED_CAST")
        val plotSpec = plotSpec as MutableMap<String, Any>
        val plotSize = if (width != null && height != null) DoubleVector(width, height) else null

        val (bitmap, _) = PlotRasterExport.exportBitmap(plotSpec, plotSize, unit, dpi, scale, canvasPeer)

        imageComparer.assertBitmapEquals(expectedFileName, bitmap)
    }
}
