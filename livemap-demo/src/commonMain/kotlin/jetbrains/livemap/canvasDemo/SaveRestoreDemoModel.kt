/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.Context2d

class SaveRestoreDemoModel(canvas: Canvas) {
    init {
        with(canvas.context2d) {
            restore()

            draw() // With default state

            // change1
            setFillStyle(Color.BLUE)
            setStrokeStyle(Color.RED)
            setGlobalAlpha(0.5)
            setFont(Context2d.Font(fontStyle = Context2d.Font.FontStyle.ITALIC, fontSize = 15.0, fontFamily = FAMILY))
            setLineWidth(4.0)
            setLineJoin(Context2d.LineJoin.ROUND)
            setLineCap(Context2d.LineCap.ROUND)
            setTextBaseline(Context2d.TextBaseline.BOTTOM)
            setTextAlign(Context2d.TextAlign.CENTER)
            setLineDash(doubleArrayOf(8.0))

            translate(0.0, 100.0)
            scale(2.0, 2.0)

            draw() // #1
            save()

            //change2

            setFillStyle(Color.DARK_MAGENTA)
            setStrokeStyle(Color.CYAN)
            setGlobalAlpha(0.8)
            setFont(Context2d.Font(fontWeight = Context2d.Font.FontWeight.BOLD, fontSize = 40.0, fontFamily = FAMILY))
            setLineWidth(8.0)
            setLineJoin(Context2d.LineJoin.MITER)
            setLineCap(Context2d.LineCap.SQUARE)
            setTextBaseline(Context2d.TextBaseline.MIDDLE)
            setTextAlign(Context2d.TextAlign.END)
            setLineDash(doubleArrayOf(8.0, 10.0))

            scale(0.5, 0.5)
            translate(0.0, 150.0)

            draw() // #2
            save()

            //change3

            setFillStyle(Color.GREEN)
            setStrokeStyle(Color.ORANGE)
            setGlobalAlpha(1.0)
            setFont(Context2d.Font(fontSize = 25.0, fontFamily = FAMILY))
            setLineWidth(2.0)
            setLineJoin(Context2d.LineJoin.BEVEL)
            setLineCap(Context2d.LineCap.BUTT)
            setTextBaseline(Context2d.TextBaseline.TOP)
            setTextAlign(Context2d.TextAlign.START)
            setLineDash(doubleArrayOf())
            translate(0.0, 100.0)
            rotate(0.3)

            draw() // #3

            restore()
            translate(400.0, 0.0)
            draw() // Like #2

            restore()
            translate(200.0, 0.0)
            draw() // Like #1

            restore()
            translate(0.0, -75.0)
            draw() // Like #1 because default context state has not been saved
        }
    }

    private fun Context2d.draw() {
        beginPath()
        moveTo(0.0, 40.0)
        lineTo(200.0,40.0)
        lineTo(160.0,80.0)
        stroke()

        fillText("Save & Restore", 100.0, 40.0)

        fillRect(20.0, 60.0, 20.0, 20.0)
        fillRect(30.0, 60.0, 20.0, 20.0)
    }

    companion object {
        const val FAMILY = "Helvetica, Arial, sans-serif"
    }
}