/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.component

import demo.livemap.common.component.DemoModelBase
import kotlinx.browser.document
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.core.platf.dom.DomMouseEventMapper
import org.jetbrains.letsPlot.platf.w3c.canvas.DomCanvasControl
import org.w3c.dom.HTMLElement

class DemoBaseJs(private val demoModelProvider: (DoubleVector) -> DemoModelBase) {
    private val size: Vector get() = Vector(800, 600)

    fun show() {
        val rootElement: HTMLElement = document.createElement("div") as HTMLElement
        val canvasControl = DomCanvasControl(
            myRootElement = rootElement,
            size = size,
            mouseEventSource = DomMouseEventMapper(rootElement, DoubleRectangle.XYWH(0.0, 0.0, size.x, size.y))
        )

        demoModelProvider(size.toDoubleVector()).show(canvasControl)

        document.getElementById(parentNodeId)
            ?.appendChild(rootElement)
            ?: error("Parent node '${parentNodeId}' wasn't found")
    }

    companion object {
        const val parentNodeId = "root"
    }
}