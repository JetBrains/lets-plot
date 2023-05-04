/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.event.dom.DomEventMapper
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.dom.DomCanvasControl
import jetbrains.livemap.demo.DemoBaseJs
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

fun baseCanvasDemo(demoModel: (canvas: Canvas, createSnapshot: (String) -> Async<Canvas.Snapshot>) -> Unit) {
    val size = Vector(800, 600)
    val rootElement: HTMLElement = document.createElement("div") as HTMLElement
    val canvasControl = DomCanvasControl(
        rootElement,
        size
    )

    DomEventMapper(rootElement, destMouseEventPeer = canvasControl.mousePeer::dispatch)

    val canvas = canvasControl.createCanvas(size)
    demoModel(canvas, canvasControl::createSnapshot)
    canvasControl.addChild(canvas)

    document.getElementById(DemoBaseJs.parentNodeId)
        ?.appendChild(rootElement)
        ?: error("Parent node '${DemoBaseJs.parentNodeId}' wasn't found")
}