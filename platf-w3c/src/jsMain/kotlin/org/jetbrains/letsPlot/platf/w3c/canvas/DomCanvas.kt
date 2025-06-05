/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.canvas

import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.ScaledCanvas
import org.jetbrains.letsPlot.platf.w3c.dom.context2d
import org.jetbrains.letsPlot.platf.w3c.dom.css.setHeight
import org.jetbrains.letsPlot.platf.w3c.dom.css.setLeft
import org.jetbrains.letsPlot.platf.w3c.dom.css.setTop
import org.jetbrains.letsPlot.platf.w3c.dom.css.setWidth
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.math.ceil
import kotlin.math.roundToInt

internal class DomCanvas private constructor(
    val canvasElement: HTMLCanvasElement,
    size: Vector,
    private val pixelRatio: Double
) : ScaledCanvas(DomContext2d(canvasElement.getContext("2d") as CanvasRenderingContext2D), size, pixelRatio) {


    override fun takeSnapshot(): Canvas.Snapshot = DomSnapshot(canvasElement, size, pixelRatio)

    internal class DomSnapshot(
        val canvasElement: HTMLCanvasElement,
        size: Vector,
        private val pixelRatio: Double
    ) : Canvas.Snapshot {
        override val size: Vector = Vector((size.x * pixelRatio).roundToInt(), (size.y * pixelRatio).roundToInt())

        override fun copy(): Canvas.Snapshot {
            val canvasCopy = createNativeCanvas(size, pixelRatio)
            canvasCopy.context2d.drawImage(canvasElement, 0.0, 0.0)
            return DomSnapshot(canvasCopy, size, pixelRatio)
        }
    }

    companion object {
        val DEVICE_PIXEL_RATIO = ceil(window.devicePixelRatio) // Fix for gaps between tiles - ratio should be integer

        fun create(size: Vector, pixelRatio: Double): DomCanvas {
            val nativeCanvas = createNativeCanvas(size, pixelRatio)
            return DomCanvas(nativeCanvas, size, pixelRatio)
        }

        private fun createNativeCanvas(
            size: Vector,
            pixelRatio: Double,
        ): HTMLCanvasElement {
            val canvasElement = document.createElement("canvas") as HTMLCanvasElement
            canvasElement.style.setWidth(size.x)
            canvasElement.style.setHeight(size.y)
            canvasElement.style.setLeft(0.0)
            canvasElement.style.setTop(0.0)
            canvasElement.style.setProperty("-webkit-user-select", "none")
            canvasElement.style.setProperty("-moz-user-select", "none")
            canvasElement.style.setProperty("-ms-user-select", "none")
            canvasElement.style.setProperty("user-select", "none")

            val width = size.x * pixelRatio
            val height = size.y * pixelRatio

            // Size should be integer. Otherwise, there are gaps between tiles.
            // This is accomplished by `ceil(window.devicePixelRatio)`.
            require(width - width.toInt() == 0.0) { "Canvas width should be integer" }
            require(height - height.toInt() == 0.0) { "Canvas height should be integer" }

            canvasElement.width = width.toInt()
            canvasElement.height = height.toInt()
            return canvasElement
        }
    }
}
