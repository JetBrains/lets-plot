/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvasDemo

import demo.livemap.demo.DemoBaseJs
import kotlinx.browser.document
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.platf.dom.DomMouseEventMapper
import org.jetbrains.letsPlot.platf.w3c.canvas.DomCanvasControl
import org.w3c.dom.HTMLElement

fun baseCanvasDemo(demoModel: (canvas: Canvas, createSnapshot: (String) -> Async<Canvas.Snapshot>) -> Unit) {
    val size = Vector(800, 600)
    val rootElement: HTMLElement = document.createElement("div") as HTMLElement
    val canvasControl = DomCanvasControl(
        myRootElement = rootElement,
        size = size,
        mouseEventSource = DomMouseEventMapper(rootElement)
    )

    val canvas = canvasControl.createCanvas(size)
    demoModel(canvas, canvasControl::createSnapshot)
    canvasControl.addChild(canvas)

    document.getElementById(DemoBaseJs.parentNodeId)
        ?.appendChild(rootElement)
        ?: error("Parent node '${DemoBaseJs.parentNodeId}' wasn't found")
}