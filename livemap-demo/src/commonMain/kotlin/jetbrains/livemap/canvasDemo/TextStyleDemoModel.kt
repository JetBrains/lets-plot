/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.Font
import jetbrains.datalore.vis.canvas.FontStyle
import jetbrains.datalore.vis.canvas.FontWeight

class TextStyleDemoModel(canvas: Canvas) {

    init {
        with(canvas.context2d) {
            val fontSize = 40.0
            val fontFamily = "serif"

            setFillStyle(Color.BLUE)

            setFont(
                Font(
                    fontSize = fontSize,
                    fontFamily = fontFamily
                )
            )

            fillText("Regular", 50.0, 50.0)

            setFont(
                Font(
                    fontWeight = FontWeight.BOLD,
                    fontSize = fontSize,
                    fontFamily = fontFamily
                )
            )

            fillText("Bold", 50.0, 100.0)

            setFont(
                Font(
                    fontStyle = FontStyle.ITALIC,
                    fontSize = fontSize,
                    fontFamily = fontFamily
                )
            )

            fillText("Italic", 50.0, 150.0)

            setFont(
                Font(
                    fontStyle = FontStyle.ITALIC,
                    fontWeight = FontWeight.BOLD,
                    fontSize = fontSize,
                    fontFamily = fontFamily
                )
            )

            fillText("Bold Italic", 50.0, 200.0)
        }
    }
}