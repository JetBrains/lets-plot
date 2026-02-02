package org.jetbrains.letsPlot.awt.canvas

import demoAndTestShared.AwtBitmapIO
import demoAndTestShared.AwtTestCanvasProvider
import demoAndTestShared.ImageComparer
import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.raster.view.PlotCanvasFigure
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import kotlin.test.Test

class CanvasPaneTest {
    @Test
    fun `should handle zero size`() {
        val pane = paneWithNotoFonts()
        pane.figure = createPlotFigure(plotSpec)

        // do layout after figure set
        pane.bounds = Rectangle(0, 0)
        pane.doLayout()

        val bufferedImage = BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB)
        val g = bufferedImage.createGraphics() as Graphics2D

        pane.paint(g)

        val bitmap = BitmapUtil.fromBufferedImage(bufferedImage)
        imageComparer.assertBitmapEquals("canvas_pane_should_handle_zero_size.png", bitmap)
    }

    @Test
    fun `figure set after layout`() {
        val pane = paneWithNotoFonts()

        // do layout before figure set
        pane.bounds = Rectangle(200, 200)
        pane.doLayout()

        // set figure after layout
        pane.figure = createPlotFigure(plotSpec)

        val bufferedImage = BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB)
        val g = bufferedImage.createGraphics() as Graphics2D

        pane.paint(g)

        val bitmap = BitmapUtil.fromBufferedImage(bufferedImage)
        imageComparer.assertBitmapEquals("canvas_pane_figure_set_after_layout.png", bitmap)
    }

    @Test
    fun `figure set before layout`() {
        val pane = paneWithNotoFonts()
        pane.figure = createPlotFigure(plotSpec)

        // do layout after figure set
        pane.bounds = Rectangle(200, 200)
        pane.doLayout()

        val bufferedImage = BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB)
        val g = bufferedImage.createGraphics() as Graphics2D

        pane.paint(g)

        val bitmap = BitmapUtil.fromBufferedImage(bufferedImage)
        imageComparer.assertBitmapEquals("canvas_pane_figure_set_before_layout.png", bitmap)
    }

    @Test
    fun `paint with clip`() {
        val pane = paneWithNotoFonts()
        pane.figure = createPlotFigure(plotSpec)
        pane.bounds = Rectangle(150, 50)
        pane.doLayout()

        val bufferedImage = BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB)
        val g = bufferedImage.createGraphics() as Graphics2D

        g.translate(25, 75)
        g.clip = Rectangle(150, 50)

        pane.paint(g)

        val bitmap = BitmapUtil.fromBufferedImage(bufferedImage)
        imageComparer.assertBitmapEquals("canvas_pane_paint_with_clip.png", bitmap)
    }

    @Test
    fun `paint on retina`() {
        val pane = paneWithNotoFonts()

        pane.figure = createPlotFigure(plotSpec)
        pane.bounds = Rectangle(150, 50)
        pane.doLayout()

        val bufferedImage = BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB)
        val g = bufferedImage.createGraphics() as Graphics2D

        // on retina display SWING scales graphics by 2.0
        g.scale(2.0, 2.0)
        g.translate(25, 75)
        g.clip = Rectangle(150, 50)

        pane.paint(g)

        val bitmap = BitmapUtil.fromBufferedImage(bufferedImage)
        imageComparer.assertBitmapEquals("canvas_pane_paint_on_retina.png", bitmap)
    }

    private fun paneWithNotoFonts(): CanvasPane {
        val pane = CanvasPane()
        pane.canvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
        return pane
    }

    private fun createPlotFigure(
        rawSpec: Map<String, Any>,
        sizingPolicy: SizingPolicy = SizingPolicy.fitContainerSize(false)
    ): PlotCanvasFigure {
        val spec = MonolithicCommon.processRawSpecs(plotSpec = rawSpec.toMutableMap(), frontendOnly = false)
        return PlotCanvasFigure().apply {
            update(
                processedSpec = spec,
                sizingPolicy = sizingPolicy,
                computationMessagesHandler = {}
            )
        }
    }

    private fun createImageComparer(): ImageComparer {
        return ImageComparer(
            tol = 5,
            canvasProvider = AwtTestCanvasProvider(),
            bitmapIO = AwtBitmapIO,
            expectedDir = System.getProperty("user.dir") + "/src/test/resources/expected-images/",
            outDir = System.getProperty("user.dir") + "/build/reports/"
        )
    }

    private val imageComparer by lazy { createImageComparer() }

    private val plotSpec = parsePlotSpec(
        """
            |{
            |  "kind": "plot",
            |  "data": {
            |    "val": [ "3", "1", "2", "3", "1", "3", "2", "1", "3", "2", "2", "2", "2", "2", "2" ],
            |    "vai": [ 3.0, 1.0, 2.0, 3.0, 1.0, 3.0, 2.0, 1.0, 3.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0 ]
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "str", "column": "val" },
            |      { "type": "int", "column": "vai" }
            |    ]
            |  },
            |  "layers": [
            |    {
            |      "geom": "bar",
            |      "mapping": { "x": "vai", "fill": "vai" },
            |      "data_meta": {
            |        "series_annotations": [
            |          {
            |            "factor_levels": [1.0, 2.0, 3.0],
            |            "column": "vai"
            |          }
            |        ]
            |      }
            |    }
            |  ]
            |}            
        """.trimMargin()
    )

}