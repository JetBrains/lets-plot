/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvasDemo

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.*
import kotlin.math.PI

class TextAlignAndBaselineDemoModel(canvas: Canvas) {

    init {
        with(canvas.context2d) {
            val fontSize = 30.0

            setFillStyle(Color.BLUE)
            setStrokeStyle(Color.RED)
            setLineWidth(2.0)
            setFont(
                Font(
                    fontWeight = FontWeight.BOLD,
                    fontSize = fontSize,
                    fontFamily = "Helvetica, Arial, sans-serif"
                )
            )

            textAndDot("Default", 200.0, 50.0)

            setTextAlign(TextAlign.START)
            textAndDot("Align Start", 200.0, 100.0)

            setTextAlign(TextAlign.CENTER)
            textAndDot("Align Center", 200.0, 150.0)

            setTextAlign(TextAlign.END)
            textAndDot("Align End", 200.0, 200.0)


            setTextAlign(TextAlign.START)
            setTextBaseline(TextBaseline.ALPHABETIC)
            textAndLine("Baseline Alphabetic", 450.0, 100.0)

            setTextBaseline(TextBaseline.BOTTOM)
            textAndLine("Baseline Bottom", 450.0, 150.0)

            setTextBaseline(TextBaseline.MIDDLE)
            textAndLine("Baseline Middle", 450.0, 200.0)

            setTextBaseline(TextBaseline.TOP)
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