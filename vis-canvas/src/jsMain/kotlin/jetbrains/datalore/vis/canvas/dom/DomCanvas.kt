/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.dom

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.js.css.setHeight
import jetbrains.datalore.base.js.css.setWidth
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.ScaledCanvas
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlinx.browser.document
import kotlinx.browser.window
import kotlin.math.ceil

internal class DomCanvas private constructor(val canvasElement: HTMLCanvasElement, size: Vector, pixelRatio: Double)
    : ScaledCanvas(DomContext2d(canvasElement.getContext("2d") as CanvasRenderingContext2D), size, pixelRatio) {

    init {
        canvasElement.style.setWidth(size.x)
        canvasElement.style.setHeight(size.y)
        canvasElement.width = ceil(size.x * pixelRatio).toInt()
        canvasElement.height = ceil(size.y * pixelRatio).toInt()
    }

    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        return Asyncs.constant(DomSnapshot())
    }

    internal inner class DomSnapshot : Canvas.Snapshot {
        val canvasElement: HTMLCanvasElement
            get() = this@DomCanvas.canvasElement
    }

    companion object {
        val DEVICE_PIXEL_RATIO = window.devicePixelRatio

        fun create(size: Vector, pixelRatio: Double): DomCanvas {
            return DomCanvas(
                document.createElement("canvas") as HTMLCanvasElement,
                size,
                pixelRatio
            )
        }
    }
}
