/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.canvas

import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.ScaledCanvas
import org.jetbrains.letsPlot.platf.w3c.dom.context2d
import org.jetbrains.letsPlot.platf.w3c.dom.css.setHeight
import org.jetbrains.letsPlot.platf.w3c.dom.css.setWidth
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.math.ceil

internal class DomCanvas private constructor(
    val canvasElement: HTMLCanvasElement,
    size: Vector,
    private val pixelRatio: Double
) : ScaledCanvas(DomContext2d(canvasElement.getContext("2d") as CanvasRenderingContext2D), size, pixelRatio) {


    override fun takeSnapshot(): Async<Canvas.Snapshot> = Asyncs.constant(DomSnapshot(canvasElement, size, pixelRatio))
    override fun immidiateSnapshot(): Canvas.Snapshot = DomSnapshot(canvasElement, size, pixelRatio)

    internal class DomSnapshot(
        val canvasElement: HTMLCanvasElement,
        private val size: Vector,
        private val pixelRatio: Double
    ) : Canvas.Snapshot {
        override fun copy(): Canvas.Snapshot {
            val canvasCopy = createNativeCanvas(size, pixelRatio)
            canvasCopy.context2d.drawImage(canvasElement, 0.0, 0.0)
            return DomSnapshot(canvasCopy, size, pixelRatio)
        }
    }

    companion object {
        val DEVICE_PIXEL_RATIO = window.devicePixelRatio

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
            canvasElement.style.setProperty("-webkit-user-select", "none")
            canvasElement.style.setProperty("-moz-user-select", "none")
            canvasElement.style.setProperty("-ms-user-select", "none")
            canvasElement.style.setProperty("user-select", "none")

            // TODO: fix it. With floor() there are gaps between tiles.
            // element size type is int so use floor to make sure that the context2d will cover whole canvas
            // e.g. width=101, ratio=2.5 => context width=202.5, canvas Width=202.
            // Without floor canvas will have width of 203 and 0.5 pixel weill not be covered by paint ops.
            canvasElement.width = ceil(size.x * pixelRatio).toInt()
            canvasElement.height = ceil(size.y * pixelRatio).toInt()
            return canvasElement
        }
    }
}
