package org.jetbrains.letsPlot.visualtesting.canvas

import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.visualtesting.ImageComparer

internal class CanvasGeneralTest(
    override val canvasPeer: CanvasPeer,
    override val imageComparer: ImageComparer,
): CanvasTestBase() {

    init {
        registerTest(::fullClear)
        registerTest(::partialClear)
    }

    private fun fullClear() {
        val (canvas, ctx) = createCanvas()

        // set context state to non-default to check that clearRect works correctly in this case
        ctx.fillStyle = "orange"
        ctx.strokeStyle = "red"
        ctx.lineWidth = 2.0
        ctx.setLineDash(doubleArrayOf(5.0, 1.0))
        ctx.setLineDashOffset(2.0)
        ctx.setFont(Font(fontStyle = FontStyle.ITALIC, fontWeight = FontWeight.BOLD, fontSize = 20.0, fontFamily = "Noto Mono"))
        ctx.setTextAlign(TextAlign.CENTER)
        ctx.setTextBaseline(TextBaseline.TOP)

        fun draw() {
            ctx.fillRect(0, 0, 20, 20)
            ctx.strokeRect(0, 0, 20, 20)

            ctx.drawCircle(25.0, 30.0, 3.0)
            ctx.strokeText("test", 25.0, 30.0)

            ctx.drawCircle(25.0, 65.0, 3.0)
            ctx.fillText("test", 25.0, 65.0)

            ctx.beginPath()
            ctx.moveTo(0.0, 0.0)
            ctx.lineTo(100.0, 100.0)
            ctx.stroke()
        }

        draw()

        ctx.clear()

        // check that context state is not reset by clearRect and can be used after it
        draw()

        assertCanvas("canvas_general_full_clear.png", canvas)
    }

    private fun partialClear() {
        val (canvas, ctx) = createCanvas()
        ctx.fillStyle = "black"
        ctx.fillRect(0.0, 0.0, canvas.size.x.toDouble(), canvas.size.y.toDouble())

        ctx.clearRect(25.0, 25.0, 50.0, 50.0)

        assertCanvas("canvas_general_partial_clear.png", canvas)
    }
}