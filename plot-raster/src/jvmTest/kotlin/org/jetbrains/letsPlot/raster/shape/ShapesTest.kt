package org.jetbrains.letsPlot.raster.shape

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Path2d
import org.jetbrains.letsPlot.core.canvas.TextMetrics
import kotlin.test.Test

class ShapesTest {

    @Test
    fun `circle bounds`() {
        val circle = Circle().apply {
            centerX = 70f
            centerY = 80f
            radius = 20f
            strokeWidth = 10f
        }

        assertThat(circle.bBoxGlobal).isEqualTo(
            DoubleRectangle.XYWH(45.0, 55.0, 50.0, 50.0)
        )
    }

    @Test
    fun `circle with zero radius bounds`() {
        val circle = Circle().apply {
            centerX = 70f
            centerY = 80f
            radius = 0f
            strokeWidth = 10f
        }

        assertThat(circle.bBoxLocal).isEqualTo(
            DoubleRectangle.XYWH(70, 80, 0, 0)
        )
    }

    @Test
    fun `ellipse bounds`() {
        val ellipse = Ellipse().apply {
            centerX = 100f
            centerY = 120f
            radiusX = 30f
            radiusY = 40f
            strokeWidth = 8f
        }

        assertThat(ellipse.bBoxGlobal).isEqualTo(
            DoubleRectangle.XYWH(66.0, 76.0, 68.0, 88.0)
        )
    }

    @Test
    fun `ellipse with zero radii bounds`() {
        val ellipse = Ellipse().apply {
            centerX = 100f
            centerY = 120f
            radiusX = 0f
            radiusY = 0f
            strokeWidth = 8f
        }

        assertThat(ellipse.bBoxLocal).isEqualTo(
            DoubleRectangle.XYWH(100.0, 120.0, 0.0, 0.0)
        )
    }

    @Test
    fun `image bounds`() {
        val image = Image().apply {
            x = 15f
            y = 25f
            width = 200f
            height = 100f
        }

        assertThat(image.bBoxLocal).isEqualTo(
            DoubleRectangle.XYWH(15.0, 25.0, 200.0, 100.0)
        )
    }

    @Test
    fun `line bounds`() {
        val line = Line().apply {
            x0 = 10f
            y0 = 20f
            x1 = 50f
            y1 = 80f
            strokeWidth = 6f
        }

        assertThat(line.bBoxGlobal).isEqualTo(
            DoubleRectangle.XYWH(7.0, 17.0, 46.0, 66.0)
        )
    }

    @Test
    fun `line zero length bounds`() {
        val line = Line().apply {
            x0 = 30f
            y0 = 40f
            x1 = 30f
            y1 = 40f
            strokeWidth = 6f
        }

        assertThat(line.bBoxLocal).isEqualTo(
            DoubleRectangle.XYWH(30, 40, 0.0, 0.0)
        )
    }

    @Test
    fun `line inverse coord bounds`() {
        val line = Line().apply {
            x0 = 50f
            y0 = 80f
            x1 = 10f
            y1 = 20f
            strokeWidth = 6f
        }

        assertThat(line.bBoxGlobal).isEqualTo(
            DoubleRectangle.XYWH(7.0, 17.0, 46.0, 66.0)
        )
    }

    @Test
    fun `path bounds`() {
        val path = Path().apply {
            pathData = Path2d()
                .moveTo(10.0, 20.0)
                .lineTo(50.0, 80.0)
                .lineTo(30.0, 10.0)
                .closePath()
            strokeWidth = 4f
        }

        assertThat(path.bBoxGlobal).isEqualTo(
            DoubleRectangle.XYWH(8.0, 8.0, 44.0, 74.0)
        )
    }

    @Test
    fun `path with zero area bounds`() {
        val path = Path().apply {
            pathData = Path2d()
                .moveTo(30.0, 40.0)
                .lineTo(30.0, 40.0)
                .closePath()
            strokeWidth = 4f
        }

        assertThat(path.bBoxLocal).isEqualTo(
            DoubleRectangle.XYWH(30.0, 40.0, 0.0, 0.0)
        )
    }

    @Test
    fun `path with no points bounds`() {
        val path = Path().apply {
            pathData = Path2d()
            strokeWidth = 4f
        }

        assertThat(path.bBoxLocal).isEqualTo(
            DoubleRectangle.XYWH(0.0, 0.0, 0.0, 0.0)
        )
    }

    @Test
    fun `rectangle bounds`() {
        val rect = Rectangle().apply {
            x = 40f
            y = 60f
            width = 120f
            height = 80f
            strokeWidth = 12f
        }

        assertThat(rect.bBoxGlobal).isEqualTo(
            DoubleRectangle.XYWH(34.0, 54.0, 132.0, 92.0)
        )
    }

    @Test
    fun `rectangle with zero size bounds`() {
        val rect = Rectangle().apply {
            x = 40f
            y = 60f
            width = 0f
            height = 0f
            strokeWidth = 12f
        }

        assertThat(rect.bBoxLocal).isEqualTo(
            DoubleRectangle.XYWH(40.0, 60.0, 0.0, 0.0)
        )
    }

    @Test
    fun `tspan bounds`() {
        val svgCanvasPeer = withTextMeasurer { str, _ ->
            when (str) {
                "Hello" -> TextMetrics(10.0, 3.0, DoubleRectangle.XYWH(0.0, -9.0, 30.0, 12.0))
                else -> error("Unexpected text: $str")
            }
        }

        val tspan = TSpan().apply {
            text = "Hello"
            peer = svgCanvasPeer
        }

        assertThat(tspan.bBoxLocal).isEqualTo(
            DoubleRectangle.XYWH(0.0, -9.0, 30.0, 12.0)
        )
    }

    @Test
    fun `text bounds`() {
        val svgCanvasPeer = withTextMeasurer { str, _ ->
            when (str) {
                "foo" -> TextMetrics(8.0, 3.0, DoubleRectangle.XYWH(0.0, -8.0, 30.0, 10.0))
                "bar" -> TextMetrics(8.0, 3.0, DoubleRectangle.XYWH(0.0, -8.0, 20.0, 12.0))
                else -> error("Unexpected text: $str")
            }
        }

        val text = Text().apply {
            peer = svgCanvasPeer
            children.addAll(listOf(
                TSpan().apply {
                    text = "foo"
                    peer = svgCanvasPeer
                },
                TSpan().apply {
                    text = "bar"
                    peer = svgCanvasPeer
                }
            ))
        }

        assertThat(text.bBoxLocal).isEqualTo(
            DoubleRectangle.XYWH(0.0, -8.0, 50.0, 12.0)
        )
    }


}