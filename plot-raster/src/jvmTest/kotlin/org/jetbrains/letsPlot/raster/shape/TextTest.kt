package org.jetbrains.letsPlot.raster.shape

import org.assertj.core.api.Assertions.assertThat
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

        val textEl = doc.element<Text>("txt")
        val spanEl = doc.element<TSpan>("span")

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

        val textEl = doc.element<Text>("txt")
        val spanEl = doc.element<TSpan>("span")

        assertThat(textEl.bBoxLocal.width).isEqualTo(10.0)

        // Dynamic Update: Change text to something very long
        // This triggers TSpan property change.
        spanEl.text = "Long"

        assertThat(textEl.bBoxLocal.left).isEqualTo(10.0)
        assertThat(textEl.bBoxLocal.width).isEqualTo(38.0)
    }
}