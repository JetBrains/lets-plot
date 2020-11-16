/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvascontrols

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.CanvasControlUtil.drawLater
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.datalore.vis.canvas.Context2d.TextAlign
import jetbrains.datalore.vis.canvas.Context2d.TextBaseline
import jetbrains.datalore.vis.canvas.SingleCanvasControl
import kotlin.math.max

internal class MessageContent(private val message: String) : CanvasContent {
    private lateinit var canvasControl: SingleCanvasControl

    override fun show(parentControl: CanvasControl) {
        canvasControl = SingleCanvasControl(parentControl)

        with(canvasControl.createCanvas()) {
            drawText(context2d, DoubleVector(size.x.toDouble(), size.y.toDouble()))

            takeSnapshot()
                .onSuccess { snapshot ->
                    drawLater(parentControl) {
                        canvasControl.context.drawImage(
                            snapshot,
                            0.0,
                            0.0
                        )
                    }
                }
        }
    }

    override fun hide() {
        canvasControl.dispose()
    }

    private fun drawText(context: Context2d, dimension: DoubleVector) =
        with(context) {
            val lines: List<String> = message.split("\n")

            save()

            setFillStyle(BACKGROUND_COLOR)
            fillRect(0.0, 0.0, dimension.x, dimension.y)

            setTextBaseline(TextBaseline.TOP)
            setTextAlign(TextAlign.START)
            setFillStyle(FONT_COLOR)
            setFont(
                Context2d.Font(
                    fontSize = FONT_SIZE,
                    fontFamily = "Helvetica, Arial, sans-serif"
                )
            )

            val height = FONT_HEIGHT * lines.size
            var width = 0.0

            lines.forEach { width = max(width, measureText(it)) }

            lines.indices.forEach {
                fillText(lines[it], (dimension.x - width) / 2, (dimension.y - height) / 2 + it * FONT_HEIGHT)
            }

            restore()
        }

    companion object {
        private const val FONT_SIZE = 17.0
        private const val FONT_HEIGHT = 21.25
        private val FONT_COLOR = Color(179, 179, 179)
        private val BACKGROUND_COLOR = Color.WHITE
    }

}