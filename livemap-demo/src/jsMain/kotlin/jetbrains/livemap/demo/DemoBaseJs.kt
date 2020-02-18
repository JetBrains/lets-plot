/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.dom.DomCanvasControl
import kotlin.browser.document

class DemoBaseJs(private val demoModelProvider: (DoubleVector) -> DemoModelBase) {
    val size: Vector get() = Vector(800, 600)

    fun show() {
        val canvasControl = DomCanvasControl(size)
        demoModelProvider(size.toDoubleVector()).show(canvasControl)

        document.getElementById(parentNodeId)
            ?.appendChild(canvasControl.rootElement)
            ?: error("Parent node '${parentNodeId}' wasn't found")
    }

    companion object {
        const val parentNodeId = "root"
    }
}