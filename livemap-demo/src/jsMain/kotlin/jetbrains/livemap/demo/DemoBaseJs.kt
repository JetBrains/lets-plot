/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import org.jetbrains.letsPlot.base.platf.dom.DomEventMapper
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.platf.w3c.canvas.DomCanvasControl
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

class DemoBaseJs(private val demoModelProvider: (DoubleVector) -> DemoModelBase) {
    private val size: Vector get() = Vector(800, 600)

    fun show() {
        val rootElement: HTMLElement = document.createElement("div") as HTMLElement
        val canvasControl = DomCanvasControl(
            rootElement,
            size
        )
        DomEventMapper(rootElement, destMouseEventPeer = canvasControl.mousePeer::dispatch)

        demoModelProvider(size.toDoubleVector()).show(canvasControl)

        document.getElementById(parentNodeId)
            ?.appendChild(rootElement)
            ?: error("Parent node '${parentNodeId}' wasn't found")
    }

    companion object {
        const val parentNodeId = "root"
    }
}