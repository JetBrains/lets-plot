/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.layers

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.minus
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.core.canvas.SingleCanvasControl
import org.jetbrains.letsPlot.core.canvas.drawImage
import jetbrains.livemap.Client
import jetbrains.livemap.core.layers.LayerKind.UI

abstract class LayerManager {
    abstract fun addLayer(name: String, layerKind: LayerKind): CanvasLayerComponent
    abstract fun removeLayer(canvasLayer: CanvasLayer)
    abstract fun pan(offset: Vec<Client>, dirtyLayers: List<CanvasLayer>)
    abstract fun repaint(dirtyLayers: List<CanvasLayer>)

    protected abstract fun repaintLayer(layer: CanvasLayer, offset: Vec<Client> = Client.ZERO_VEC)

    private val children = HashMap<LayerKind, MutableList<CanvasLayer>>()
    val layers: MutableList<CanvasLayer> = mutableListOf()

    protected fun add(kind: LayerKind, layer: CanvasLayer) {
        children.getOrPut(kind, ::ArrayList).add(layer)
        layers.clear()
        layers.addAll(LayerKind.values().flatMap { children[it] ?: emptyList() })
    }

    protected fun remove(layer: CanvasLayer) {
        children[layer.kind]?.remove(layer)
        layers.remove(layer)
    }
}

class OffscreenLayerManager(canvasControl: CanvasControl) : LayerManager() {
    private val singleCanvasControl: SingleCanvasControl = SingleCanvasControl(canvasControl)
    private val rect: DoubleRectangle = DoubleRectangle(DoubleVector.ZERO, canvasControl.size.toDoubleVector())
    private val myBackingStore = mutableMapOf<CanvasLayer, Canvas.Snapshot>()
    private val myPanningOffsets = mutableMapOf<CanvasLayer, Vec<Client>>()

    override fun pan(offset: Vec<Client>, dirtyLayers: List<CanvasLayer>) {
        singleCanvasControl.context.clearRect(rect)

        layers.forEach { layer ->
            when (layer.panningPolicy) {
                PanningPolicy.COPY -> panLayer(layer, offset)
                PanningPolicy.REPAINT -> {
                    if (layer in dirtyLayers) {
                        repaintLayer(layer, offset)
                    }
                    panLayer(layer, offset)
                }
            }
        }
    }

    override fun repaint(dirtyLayers: List<CanvasLayer>) {
        if (dirtyLayers.isEmpty()) return

        dirtyLayers.forEach(::repaintLayer)

        singleCanvasControl.context.clearRect(rect)
        layers.forEach {
            myBackingStore[it]?.let(singleCanvasControl.context::drawImage)
        }
    }

    private fun panLayer(layer: CanvasLayer, offset: Vec<Client>) {
        when (layer.kind) {
            UI -> Client.ZERO_VEC
            else -> offset - (myPanningOffsets[layer] ?: Client.ZERO_VEC)
        }.let { p ->
            myBackingStore[layer]?.let { snapshot ->
                singleCanvasControl.context.drawImage(snapshot, p)
            }
        }
    }

    override fun repaintLayer(layer: CanvasLayer, offset: Vec<Client>) {
        layer.clear()
        layer.render()
        myBackingStore[layer] = layer.snapshot()
        myPanningOffsets[layer] = offset
    }

    override fun addLayer(name: String, layerKind: LayerKind): CanvasLayerComponent {
        val canvasLayer = CanvasLayer(singleCanvasControl.createCanvas(), name, layerKind)
        add(layerKind, canvasLayer)
        return CanvasLayerComponent(canvasLayer)
    }

    override fun removeLayer(canvasLayer: CanvasLayer) {
        remove(canvasLayer)
    }
}

class ScreenLayerManager(
    private val canvasControl: CanvasControl,
) : LayerManager() {
    private val myBackingStore = mutableMapOf<CanvasLayer, Canvas.Snapshot>()
    private val myPanningOffsets = mutableMapOf<CanvasLayer, Vec<Client>>()

    override fun pan(offset: Vec<Client>, dirtyLayers: List<CanvasLayer>) {
        layers.forEach { layer ->
            when (layer.panningPolicy) {
                PanningPolicy.COPY -> panLayer(layer, offset)
                PanningPolicy.REPAINT -> {
                    if (layer in dirtyLayers) {
                        repaintLayer(layer, offset)
                        myBackingStore[layer] = layer.snapshot()
                        myPanningOffsets[layer] = offset
                    }
                    if (layer !in myBackingStore) {
                        myBackingStore[layer] = layer.snapshot()
                        myPanningOffsets[layer] = offset
                    }
                    panLayer(layer, offset)
                }
            }
        }
    }

    override fun repaint(dirtyLayers: List<CanvasLayer>) {
        if (dirtyLayers.isEmpty()) return

        dirtyLayers.forEach {
            myBackingStore.remove(it)
            myPanningOffsets.remove(it)
            repaintLayer(it)
        }
    }

    override fun addLayer(name: String, layerKind: LayerKind): CanvasLayerComponent {
        val canvas = canvasControl.createCanvas(canvasControl.size)
        val canvasLayer = CanvasLayer(canvas, name, layerKind)
        add(layerKind, canvasLayer)

        canvasControl.addChild(layers.indexOf(canvasLayer), canvas)
        return CanvasLayerComponent(canvasLayer)
    }

    override fun removeLayer(canvasLayer: CanvasLayer) {
        canvasLayer.removeFrom(canvasControl)
        remove(canvasLayer)
    }

    private fun panLayer(layer: CanvasLayer, offset: Vec<Client>) {
        when (layer.kind) {
            UI -> {}
            else -> {
                myBackingStore.getOrPut(layer, layer::snapshot)
                    .let { snapshot ->
                        layer.clear()
                        layer.canvas.context2d.drawImage(snapshot, offset - (myPanningOffsets[layer] ?: Client.ZERO_VEC))
                }
            }
        }
    }

    override fun repaintLayer(layer: CanvasLayer, offset: Vec<Client>) {
        layer.clear()
        layer.render()
    }
}
