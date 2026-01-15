package org.jetbrains.letsPlot.visualtesting.canvas

import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.letsPlot.visualtesting.ImageComparer

internal class CanvasTextTest(
    override val canvasPeer: CanvasPeer,
    override val imageComparer: ImageComparer,
): CanvasTestBase() {
    init {
        registerTest(::monospace)
        registerTest(::monospaceItalic)
        registerTest(::monospaceBold)
        registerTest(::monospaceBoldItalic)
    }

    private fun monospace() {
        // Test that regular monospaced font is rendered correctly
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.setFont(Font(fontFamily = "Noto Sans Mono", fontSize = 16.0))
        ctx.fillText("monospace", 5.0, 30.0)

        assertCanvas("text_monospace.png", canvas)
    }

    private fun monospaceItalic() {
        val fontSize = 16.0

        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.setFont(Font(fontFamily = "Noto Sans Mono", fontSize = fontSize, fontStyle = FontStyle.ITALIC))
        ctx.fillText("monospace", 5.0, 30.0)

        ctx.translate(5.0, 70.0)
        ctx.fillText("monospace", 0.0, 0.0)

        assertCanvas("text_monospace_italic.png", canvas)
    }

    private fun monospaceBold() {
        val fontSize = 16.0

        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.setFont(Font(fontFamily = "Noto Sans Mono", fontSize = fontSize, fontWeight = FontWeight.BOLD))
        ctx.fillText("monospace", 5.0, 30.0)

        assertCanvas("text_monospace_bold.png", canvas)
    }

    private fun monospaceBoldItalic() {
        val fontSize = 16.0
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.setFont(Font(
            fontStyle = FontStyle.ITALIC,
            fontWeight = FontWeight.BOLD,
            fontSize = fontSize,
            fontFamily = "Noto Sans Mono"
        ))
        ctx.fillText("monospace", 0.0, 30.0)

        assertCanvas("text_monospace_bold_italic.png", canvas)
    }

}
