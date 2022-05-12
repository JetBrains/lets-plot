/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.layers

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.Context2d

class CanvasLayer(
    val canvas: Canvas,
    val name: String,
    val kind: LayerKind
) {
    private val myRect = DoubleRectangle(0.0, 0.0, canvas.size.x.toDouble(), canvas.size.y.toDouble())
    private val myRenderTaskList = ArrayList<(Context2d) -> Unit>()

    val size: Vector
        get() = canvas.size

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

val CanvasLayer.panningPolicy: PanningPolicy get() = when (kind) {
    LayerKind.BASEMAP_TILES -> PanningPolicy.REPAINT
    LayerKind.FEATURES -> PanningPolicy.COPY
    LayerKind.BASEMAP_LABELS -> PanningPolicy.REPAINT
    LayerKind.UI -> PanningPolicy.REPAINT
}