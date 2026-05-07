/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.visualtesting.plot

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.raster.export.PlotRasterExport
import org.jetbrains.letsPlot.raster.view.PlotCanvasDrawable
import org.jetbrains.letsPlot.visualtesting.TestSuitBase

abstract class PlotTestSuitBase : TestSuitBase() {
    private val plotHelper by lazy { PlotHelper(canvasPeer = canvasPeer) }

    fun createPlot(
        plotSpec: MutableMap<String, Any?>,
        width: Number? = null,
        height: Number? = null,
        renderingHints: Map<Any, Any> = emptyMap()
    ): PlotCanvasDrawable {
        return plotHelper.createPlot(plotSpec, width, height, renderingHints)
    }

    fun paint(plotCanvasDrawable: PlotCanvasDrawable, cursorPos: Vector? = null): Bitmap {
        return plotHelper.paint(plotCanvasDrawable, cursorPos)
    }

    fun paint(
        plotSpec: MutableMap<String, Any?>,
        width: Number? = null,
        height: Number? = null,
        unit: SizeUnit? = null,
        dpi: Number? = null,
        scale: Number? = 1.0
    ): Bitmap {
        @Suppress("UNCHECKED_CAST")
        val plotSpec = plotSpec as MutableMap<String, Any>
        val plotSize = if (width != null && height != null) DoubleVector(width, height) else null

        val (bitmap, _) = PlotRasterExport.exportBitmap(plotSpec, plotSize, unit, dpi, scale, canvasPeer)
        return bitmap
    }
}
