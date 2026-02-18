package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.raster.view.PlotCanvasDrawable
import org.jetbrains.letsPlot.visualtesting.AwtBitmapIO
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import kotlin.test.Test

class CanvasComponentTest {
    @Test
    fun `should handle zero size`() {
        val canvasComponent = createCanvasComponent()
        canvasComponent.content = createPlotFigure(plotSpec)

        // do layout after figure set
        canvasComponent.bounds = Rectangle(0, 0)
        canvasComponent.doLayout()

        val bufferedImage = BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB)
        val g = bufferedImage.createGraphics() as Graphics2D

        canvasComponent.paint(g)

        val bitmap = BitmapUtil.fromBufferedImage(bufferedImage)
        imageComparer.assertBitmapEquals("canvas_component_should_handle_zero_size.png", bitmap)
    }

    @Test
    fun `figure set after layout`() {
        val canvasComponent = createCanvasComponent()

        // do layout before figure set
        canvasComponent.bounds = Rectangle(200, 200)
        canvasComponent.doLayout()

        // set figure after layout
        canvasComponent.content = createPlotFigure(plotSpec)

        val bufferedImage = BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB)
        val g = bufferedImage.createGraphics() as Graphics2D

        canvasComponent.paint(g)

        val bitmap = BitmapUtil.fromBufferedImage(bufferedImage)
        imageComparer.assertBitmapEquals("canvas_component_figure_set_after_layout.png", bitmap)
    }

    @Test
    fun `figure set before layout`() {
        val canvasComponent = createCanvasComponent()
        canvasComponent.content = createPlotFigure(plotSpec)

        // do layout after figure set
        canvasComponent.bounds = Rectangle(200, 200)
        canvasComponent.doLayout()

        val bufferedImage = BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB)
        val g = bufferedImage.createGraphics() as Graphics2D

        canvasComponent.paint(g)

        val bitmap = BitmapUtil.fromBufferedImage(bufferedImage)
        imageComparer.assertBitmapEquals("canvas_component_figure_set_before_layout.png", bitmap)
    }

    @Test
    fun `paint with clip`() {
        val canvasComponent = createCanvasComponent()
        canvasComponent.content = createPlotFigure(plotSpec)
        canvasComponent.bounds = Rectangle(150, 50)
        canvasComponent.doLayout()

        val bufferedImage = BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB)
        val g = bufferedImage.createGraphics() as Graphics2D

        g.translate(25, 75)
        g.clip = Rectangle(150, 50)

        canvasComponent.paint(g)

        val bitmap = BitmapUtil.fromBufferedImage(bufferedImage)
        imageComparer.assertBitmapEquals("canvas_component_paint_with_clip.png", bitmap)
    }

    @Test
    fun `paint on retina`() {
        val canvasComponent = createCanvasComponent()

        canvasComponent.content = createPlotFigure(plotSpec)
        canvasComponent.bounds = Rectangle(150, 50)
        canvasComponent.doLayout()

        val bufferedImage = BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB)
        val g = bufferedImage.createGraphics() as Graphics2D

        // on retina display SWING scales graphics by 2.0
        g.scale(2.0, 2.0)
        g.translate(25, 75)
        g.clip = Rectangle(150, 50)

        canvasComponent.paint(g)

        val bitmap = BitmapUtil.fromBufferedImage(bufferedImage)
        imageComparer.assertBitmapEquals("canvas_component_paint_on_retina.png", bitmap)
    }

    private fun createCanvasComponent(): CanvasComponent {
        val component = CanvasComponent()
        component.canvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
        return component
    }

    private fun createPlotFigure(
        rawSpec: Map<String, Any>,
        sizingPolicy: SizingPolicy = SizingPolicy.fitContainerSize(false)
    ): PlotCanvasDrawable {
        val spec = MonolithicCommon.processRawSpecs(plotSpec = rawSpec.toMutableMap(), frontendOnly = false)
        return PlotCanvasDrawable().apply {
            update(
                processedSpec = spec,
                sizingPolicy = sizingPolicy,
                computationMessagesHandler = {}
            )
        }
    }

    private fun createImageComparer(): ImageComparer {
        val canvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
        val awtBitmapIO = AwtBitmapIO(
            expectedImagesDir = "/src/test/resources/expected-images/canvascomponent",
            outputDir = "/build/reports/actual-images/canvascomponent"
        )

        return ImageComparer(canvasPeer, awtBitmapIO, silent = true)
    }

    private val imageComparer by lazy { createImageComparer() }

    @Suppress("UNCHECKED_CAST")
    private val plotSpec = JsonSupport.parseJson(
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
    ) as Map<String, Any>

}