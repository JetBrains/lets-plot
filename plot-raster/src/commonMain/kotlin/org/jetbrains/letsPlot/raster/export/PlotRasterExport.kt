package org.jetbrains.letsPlot.raster.export

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.PlotExportCommon
import org.jetbrains.letsPlot.raster.view.PlotCanvasDrawable
import org.jetbrains.letsPlot.raster.view.RenderingHints

object PlotRasterExport {
        fun exportBitmap(
            plotSpec: Map<String, Any>,
            plotSize: DoubleVector? = null,
            sizeUnit: PlotExportCommon.SizeUnit? = null,
            dpi: Number? = null,
            scale: Number? = null,
            canvasPeer: CanvasPeer,
        ): Pair<Bitmap, Double> {
            val exportParameters = PlotExportCommon.computeExportParameters(plotSize, dpi, sizeUnit, scale)

            @Suppress("UNCHECKED_CAST")
            val rawPlotSpec = plotSpec as MutableMap<String, Any>

            val plotCanvasDrawable = PlotCanvasDrawable()

            plotCanvasDrawable.setRenderingHint(
                RenderingHints.KEY_OFFSCREEN_BUFFERING,
                RenderingHints.VALUE_OFFSCREEN_BUFFERING_OFF
            )

            plotCanvasDrawable.update(
                processedSpec = MonolithicCommon.processRawSpecs(rawPlotSpec, frontendOnly = false),
                sizingPolicy = exportParameters.sizingPolicy,
                computationMessagesHandler = { }
            )

            var canvasReg: Registration? = plotCanvasDrawable.mapToCanvas(canvasPeer)

            try {
                val canvas = canvasPeer.createCanvas(
                    plotCanvasDrawable.size,
                    contentScale = exportParameters.scaleFactor
                )
                val ctx = canvas.context2d
                plotCanvasDrawable.paint(ctx)

                val snapshot = canvas.takeSnapshot()
                val bitmap = snapshot.bitmap

                canvasReg?.dispose()
                canvasReg = null

                ctx.dispose()
                snapshot.dispose()

                return bitmap to exportParameters.dpi
            } finally {
                canvasReg?.dispose()
            }
        }
}