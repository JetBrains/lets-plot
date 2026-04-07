/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.visualtesting.canvas

import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.visualtesting.ImageComparer.ComparisonProfile
import org.jetbrains.letsPlot.visualtesting.TestSuit


abstract class CanvasTestBase : TestSuit() {
    override val defaultComparisonProfile: ComparisonProfile = ComparisonProfile.Geometries

    fun createCanvas(width: Int = 100, height: Int = 100): Pair<Canvas, Context2d> {
        val canvas = canvasPeer.createCanvas(width = width, height = height)
        val context2d = canvas.context2d
        return canvas to context2d
    }

    companion object {
        var Context2d.lineWidth: Double
            get() = error("lineWidth is write only")
            set(value) {
                setLineWidth(value)
            }

        var Context2d.fillStyle: Any?
            get() = error("fillStyle is write only")
            set(value) {
                val color = when (value) {
                    is Color -> value
                    is String -> Colors.parseColor(value)
                    null -> null
                    else -> error("Unsupported fill style: $value")
                }

                setFillStyle(color)
            }

        var Context2d.strokeStyle: Any?
            get() = error("strokeStyle is write only")
            set(value) {
                val color = when (value) {
                    is Color -> value
                    is String -> Colors.parseColor(value)
                    null -> null
                    else -> error("Unsupported fill style: $value")
                }

                setStrokeStyle(color)
            }
    }

    fun paint(canvas: Canvas): Bitmap {
        val snapshot = canvas.takeSnapshot()
        val bitmap = snapshot.bitmap
        snapshot.dispose()
        return bitmap
    }
}
