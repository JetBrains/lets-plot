/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.dom

import org.jetbrains.letsPlot.base.intern.async.Async
import org.jetbrains.letsPlot.base.intern.async.Asyncs
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.js.css.setHeight
import jetbrains.datalore.base.js.css.setWidth
import jetbrains.datalore.base.js.dom.context2d
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.ScaledCanvas
import kotlinx.browser.document
import kotlinx.browser.window
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
