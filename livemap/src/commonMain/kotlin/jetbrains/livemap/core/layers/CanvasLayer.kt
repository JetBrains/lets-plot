/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.layers

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.layers.PanningPolicy.REPAINT

class CanvasLayer(
    val canvas: Canvas,
    val name: String,
    val kind: LayerKind
) {
    private val myRect = DoubleRectangle(0.0, 0.0, canvas.size.x.toDouble(), canvas.size.y.toDouble())
    private val myRenderTaskList = ArrayList<(Context2d) -> Unit>()

    val size: Vector
        get() = canvas.size

    var panningPolicy = REPAINT

    fun addRenderTask(func: (Context2d) -> Unit) {
        myRenderTaskList.add(func)
    }

    fun clearRenderTaskss() {
        myRenderTaskList.clear()
    }

    fun render() {
        val context2d = canvas.context2d
        myRenderTaskList.forEach { it(context2d) }
        myRenderTaskList.clear()
    }

    fun snapshot(): Canvas.Snapshot = canvas.immidiateSnapshot()

    fun clear() {
        canvas.context2d.clearRect(myRect)
    }

    fun removeFrom(canvasControl: CanvasControl) {
        canvasControl.removeChild(canvas)
    }

    override fun toString(): String {
        return "layer_" + name
    }

    fun containsRenderTasks() = myRenderTaskList.isNotEmpty()
}

enum class LayerKind {
    BASEMAP_TILES,
    FEATURES,
    BASEMAP_LABELS,
    UI
}

enum class PanningPolicy {
    COPY, REPAINT
}
