/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.Context2d
import kotlin.math.PI

class DrawTextDemoModel(canvas: Canvas) {

    init {
        with(canvas.context2d) {
            val fontSize = 30.0

            setFillStyle(Color.BLUE)
            setStrokeStyle(Color.RED)
            setLineWidth(2.0)
            setFont(
                Context2d.Font(
                    fontWeight = Context2d.Font.FontWeight.BOLD,
                    fontSize = fontSize,
                    fontFamily = "Helvetica, Arial, sans-serif"
                )
            )

            textAndDot("Default", 200.0, 50.0)

            setTextAlign(Context2d.TextAlign.START)
            textAndDot("Align Start", 200.0, 100.0)

            setTextAlign(Context2d.TextAlign.CENTER)
            textAndDot("Align Center", 200.0, 150.0)

            setTextAlign(Context2d.TextAlign.END)
            textAndDot("Align End", 200.0, 200.0)


            setTextAlign(Context2d.TextAlign.START)
            setTextBaseline(Context2d.TextBaseline.ALPHABETIC)
            textAndLine("Baseline Alphabetic", 450.0, 100.0)

            setTextBaseline(Context2d.TextBaseline.BOTTOM)
            textAndLine("Baseline Bottom", 450.0, 150.0)

            setTextBaseline(Context2d.TextBaseline.MIDDLE)
            textAndLine("Baseline Middle", 450.0, 200.0)

            setTextBaseline(Context2d.TextBaseline.TOP)
            textAndLine("Baseline Top", 450.0, 250.0)
        }
    }

     private fun Context2d.textAndDot(text: String, x: Double, y: Double) {
         fillText(text, x, y)

         beginPath()
         arc(x, y, 2.0, 0.0, 2 * PI)
         stroke()
     }

    private fun Context2d.textAndLine(text: String, x: Double, y: Double) {
        fillText(text, x, y)

        beginPath()
        moveTo(x, y)
        lineTo(x + 300, y)
        stroke()
    }
}