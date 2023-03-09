/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.event.dom.DomEventMapper
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.dom.DomCanvasControl
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