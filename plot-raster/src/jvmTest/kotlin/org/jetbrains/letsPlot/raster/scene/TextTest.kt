package org.jetbrains.letsPlot.raster.scene

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgColors
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgColors.DARK_GRAY
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgColors.LIGHT_GRAY
import kotlin.test.Ignore
import kotlin.test.Test

class TextTest {

    @Test
    fun `dynamic update - moving text should update child tspan bbox`() {

        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                text(x = 10, y = 10, id = "txt") {
                    tspan("Hello", id = "span")
                }
            }
        }

        val textEl = doc.findElement<Text>("txt")
        val spanEl = doc.findElement<TSpan>("span")

        // Layout ran during creation, everything is correct.
        assertThat(textEl.bBoxLocal.left).isEqualTo(10.0)
        assertThat(spanEl.bBoxLocal.left).isEqualTo(10.0)

        // Move the Text container
        // This sets 'needLayout = true' in Text.
        textEl.x = 100f

        assertThat(textEl.bBoxLocal.left)
            .describedAs("Text local bbox should move t0.0o new X")
            .isEqualTo(100.0)

        assertThat(spanEl.bBoxLocal.left)
            .describedAs("TSpan local bbox should reflect .0parent Text's new X")
            .isEqualTo(100.0)
    }

    @Test
    fun `dynamic update - changing text content should update text bbox`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                text(x = 10, y = 10, id = "txt") {
                    tspan("S", id = "span") // Short string
                }
            }
        }

        val textEl = doc.findElement<Text>("txt")
        val spanEl = doc.findElement<TSpan>("span")

        assertThat(textEl.bBoxLocal.width).isEqualTo(10.0)

        // Dynamic Update: Change text to something very long
        // This triggers TSpan property change.
        spanEl.text = "Long"

        assertThat(textEl.bBoxLocal.left).isEqualTo(10.0)
        assertThat(textEl.bBoxLocal.width).isEqualTo(38.0)
    }

    @Test
    fun `dynamic update - horizontal alignment`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                text("Left", x = 0, y = 10, id = "leftAligned", anchor = "start")
                text("Right", x = 400, y = 10,id = "rightAligned")
            }
        }

        val leftAligned = doc.findElement<Text>("leftAligned")
        val rightAligned = doc.findElement<Text>("rightAligned")

        assertThat(leftAligned.bBoxLocal.left).isEqualTo(0.0)
        assertThat(rightAligned.bBoxLocal.left).isEqualTo(400.0)

        rightAligned.textAlignment = Text.HorizontalAlignment.RIGHT

        assertThat(rightAligned.bBoxLocal.left).isEqualTo(352.0)
    }

    @Test
    fun `text property should not override non default tspan property`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                text(fill = SvgColors.BLUE, stroke = SvgColors.RED, strokeWidth = 2, id = "text") {
                    tspan(text = "Hello", fill = SvgColors.GREEN, id = "tspan")
                }
            }
        }

        doc.findElement<TSpan>("tspan").let {
            assertThat(it.fill).isEqualTo(Color.GREEN)
            assertThat(it.stroke).isEqualTo(Color.RED)
            assertThat(it.strokeWidth).isEqualTo(2f)
        }
    }

    @Test
    fun `text property should not override non default tspan property on second update`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                text(fill = SvgColors.BLUE, stroke = SvgColors.RED, strokeWidth = 2, id = "text") {
                    tspan(text = "Hello", fill = SvgColors.GREEN, id = "tspan")
                }
            }
        }

        doc.findElement<Text>("text").let {
            it.fill = Color.YELLOW // tspan will ignore this value
            it.stroke = Color.BLACK
        }

        doc.findElement<TSpan>("tspan").let {
            assertThat(it.fill).isEqualTo(Color.GREEN) // Same as in initialization
            assertThat(it.stroke).isEqualTo(Color.BLACK) // Should inherit updated stroke
            assertThat(it.strokeWidth).isEqualTo(2f)
        }
    }

    @Test
    fun `tspan without attr and style in parent`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                text(id = "txt-simple") {
                    tspan(text = "A", id = "A")
                    tspan(text = "B", stroke = LIGHT_GRAY, id = "B")
                    tspan(text = "C", stroke = LIGHT_GRAY, fill = DARK_GRAY, id = "C")
                    tspan(text = "D", stroke = LIGHT_GRAY, fill = DARK_GRAY, strokeWidth = 5, id = "D")
                }
            }
        }

        doc.findElement<TSpan>("A").let {
            assertThat(it.stroke).isNull()
            assertThat(it.fill).isEqualTo(Color.BLACK)
            assertThat(it.strokeWidth).isEqualTo(1f)
        }

        doc.findElement<TSpan>("B").let {
            assertThat(it.stroke).isEqualTo(Color.LIGHT_GRAY)
            assertThat(it.fill).isEqualTo(Color.BLACK)
            assertThat(it.strokeWidth).isEqualTo(1f)
        }

        doc.findElement<TSpan>("C").let {
            assertThat(it.stroke).isEqualTo(Color.LIGHT_GRAY)
            assertThat(it.fill).isEqualTo(Color.DARK_GRAY)
            assertThat(it.strokeWidth).isEqualTo(1f)
        }

        doc.findElement<TSpan>("D").let {
            assertThat(it.stroke).isEqualTo(Color.LIGHT_GRAY)
            assertThat(it.fill).isEqualTo(Color.DARK_GRAY)
            assertThat(it.strokeWidth).isEqualTo(5f)
        }
    }

    @Test
    fun `tspan with attr in parent`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                text(id = "txt-attr", stroke = SvgColors.GREEN, fill = SvgColors.BLUE, strokeWidth = 2) {
                    tspan(text = "A", id = "A")
                    tspan(text = "B", stroke = LIGHT_GRAY, id = "B")
                    tspan(text = "C", stroke = LIGHT_GRAY, fill = DARK_GRAY, id = "C")
                    tspan(text = "D", stroke = LIGHT_GRAY, fill = DARK_GRAY, strokeWidth = 5, id = "D")
                }
            }
        }

        doc.findElement<TSpan>("A").let {
            assertThat(it.stroke).isEqualTo(Color.GREEN)
            assertThat(it.fill).isEqualTo(Color.BLUE)
            assertThat(it.strokeWidth).isEqualTo(2f)
        }

        doc.findElement<TSpan>("B").let {
            assertThat(it.stroke).isEqualTo(Color.LIGHT_GRAY)
            assertThat(it.fill).isEqualTo(Color.BLUE)
            assertThat(it.strokeWidth).isEqualTo(2f)
        }

        doc.findElement<TSpan>("C").let {
            assertThat(it.stroke).isEqualTo(Color.LIGHT_GRAY)
            assertThat(it.fill).isEqualTo(Color.DARK_GRAY)
            assertThat(it.strokeWidth).isEqualTo(2f)
        }

        doc.findElement<TSpan>("D").let {
            assertThat(it.stroke).isEqualTo(Color.LIGHT_GRAY)
            assertThat(it.fill).isEqualTo(Color.DARK_GRAY)
            assertThat(it.strokeWidth).isEqualTo(5f)
        }
    }


    @Ignore("Fails due to conflict between style and attribute precedence")
    @Test
    fun `tspan with style in parent`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                style(
                    """
                    .text-style {
                        fill: #ff0000;
                        stroke: #00ff00;
                        stroke-width: 3;
                    }
                    """.trimIndent()
                )

                text(id = "txt-style", styleClass = "text-style") {
                    tspan(text = "A", id = "A")
                    tspan(text = "B", stroke = LIGHT_GRAY, id = "B")
                    tspan(text = "C", stroke = LIGHT_GRAY, fill = DARK_GRAY, id = "C")
                    tspan(text = "D", stroke = LIGHT_GRAY, fill = DARK_GRAY, strokeWidth = 5, id = "D")
                }
            }
        }

        doc.findElement<TSpan>("A").let {
            assertThat(it.fill).isEqualTo(Color.RED)
            assertThat(it.stroke).isEqualTo(Color.GREEN)
            assertThat(it.strokeWidth).isEqualTo(3f)
        }

        doc.findElement<TSpan>("B").let {
            assertThat(it.fill).isEqualTo(Color.RED)
            assertThat(it.stroke).isEqualTo(Color.LIGHT_GRAY)
            assertThat(it.strokeWidth).isEqualTo(3f)
        }

        doc.findElement<TSpan>("C").let {
            assertThat(it.fill).isEqualTo(Color.DARK_GRAY)
            assertThat(it.stroke).isEqualTo(Color.LIGHT_GRAY)
            assertThat(it.strokeWidth).isEqualTo(3f)
        }

        doc.findElement<TSpan>("D").let {
            assertThat(it.fill).isEqualTo(Color.DARK_GRAY)
            assertThat(it.stroke).isEqualTo(Color.LIGHT_GRAY)
            assertThat(it.strokeWidth).isEqualTo(5f)
        }
    }

    @Ignore("Fails due to conflict between style and attribute precedence")
    @Test
    fun `tspan with style and attr in parent`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                style(
                    """
                    .text-style {
                        stroke: #00ff00;
                        fill: #ff0000;
                        stroke-width: 3;
                    }
                    """.trimIndent()
                )

                text(id = "txt-attr-style", stroke = SvgColors.GREEN, fill = SvgColors.BLUE, strokeWidth = 2, styleClass = "text-style") {
                    tspan(text = "A", id = "A")
                    tspan(text = "B", stroke = LIGHT_GRAY, id = "B")
                    tspan(text = "C", stroke = LIGHT_GRAY, fill = DARK_GRAY, id = "C")
                    tspan(text = "D", stroke = LIGHT_GRAY, fill = DARK_GRAY, strokeWidth = 5, id = "D")
                }
            }
        }

        doc.findElement<TSpan>("tspan").let {
            assertThat(it.fill).isEqualTo(Color.RED) // from style
            assertThat(it.stroke).isEqualTo(Color.GREEN) // from attribute
            assertThat(it.strokeWidth).isEqualTo(3f) // from style
        }
    }
}