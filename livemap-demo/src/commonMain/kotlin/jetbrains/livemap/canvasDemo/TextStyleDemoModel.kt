/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.Context2d

class TextStyleDemoModel(canvas: Canvas) {

    init {
        with(canvas.context2d) {
            val fontSize = 40.0
            val fontFamily = "serif"

            setFillStyle(Color.BLUE)

            setFont(
                Context2d.Font(
                    fontSize = fontSize,
                    fontFamily = fontFamily
                )
            )

            fillText("Regular", 50.0, 50.0)

            setFont(
                Context2d.Font(
                    fontWeight = Context2d.Font.FontWeight.BOLD,
                    fontSize = fontSize,
                    fontFamily = fontFamily
                )
            )

            fillText("Bold", 50.0, 100.0)

            setFont(
                Context2d.Font(
                    fontStyle = Context2d.Font.FontStyle.ITALIC,
                    fontSize = fontSize,
                    fontFamily = fontFamily
                )
            )

            fillText("Italic", 50.0, 150.0)

            setFont(
                Context2d.Font(
                    fontStyle = Context2d.Font.FontStyle.ITALIC,
                    fontWeight = Context2d.Font.FontWeight.BOLD,
                    fontSize = fontSize,
                    fontFamily = fontFamily
                )
            )

            fillText("Bold Italic", 50.0, 200.0)
        }
    }
}