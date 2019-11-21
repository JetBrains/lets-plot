/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering.layers

import jetbrains.datalore.base.async.PlatformAsyncs
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.SingleCanvasControl
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.rendering.layers.LayersRenderingSystem.RenderingStrategy
import jetbrains.livemap.core.rendering.layers.RenderTarget.*

object LayerManagers {

    fun createLayerManager(
        componentManager: EcsComponentManager,
        renderTarget: RenderTarget,
        canvasControl: CanvasControl
    ): LayerManager {
        return when (renderTarget) {
            SINGLE_SCREEN_CANVAS -> singleScreenCanvas(canvasControl, componentManager)

            OWN_OFFSCREEN_CANVAS -> offscreenLayers(canvasControl, componentManager)

            OWN_SCREEN_CANVAS -> screenLayers(canvasControl, componentManager)
        }
    }


    private fun singleScreenCanvas(canvasControl: CanvasControl, componentManager: EcsComponentManager): LayerManager {
        val singleCanvasControl = SingleCanvasControl(canvasControl)
        val rect = DoubleRectangle(DoubleVector.ZERO, canvasControl.size.toDoubleVector())
        return object : LayerManager {
            private val myGroupedLayers = GroupedLayers()

            override fun createLayerRenderingSystem(): LayersRenderingSystem {
                return LayersRenderingSystem(
                    componentManager,
                    object : RenderingStrategy {
                        override fun render(
                            renderingOrder: List<CanvasLayer>,
                            layerEntities: Iterable<EcsEntity>,
                            dirtyLayerEntities: Iterable<EcsEntity>
                        ) {
                            singleCanvasControl.context.clearRect(rect)
                            renderingOrder.forEach(CanvasLayer::render)
                            // Force render tasks to be added
                            layerEntities.forEach { it.tag(::DirtyCanvasLayerComponent) }
                        }

                    }
                )
            }

            override fun addLayer(name: String, group: LayerGroup): CanvasLayerComponent {
                val canvasLayer = CanvasLayer(singleCanvasControl.canvas, name)
                myGroupedLayers.add(group, canvasLayer)
                return CanvasLayerComponent(canvasLayer)
            }

            override fun removeLayer(group: LayerGroup, canvasLayer: CanvasLayer) {
                myGroupedLayers.remove(group, canvasLayer)
            }

            override fun createLayersOrderComponent(): LayersOrderComponent {
                return LayersOrderComponent(myGroupedLayers)
            }
        }
    }

    private fun offscreenLayers(canvasControl: CanvasControl, componentManager: EcsComponentManager): LayerManager {
        val singleCanvasControl = SingleCanvasControl(canvasControl)
        val rect = DoubleRectangle(DoubleVector.ZERO, canvasControl.size.toDoubleVector())

        return object : LayerManager {
            private val myGroupedLayers = GroupedLayers()

            override fun createLayerRenderingSystem(): LayersRenderingSystem {
                return LayersRenderingSystem(
                    componentManager,
                    object : RenderingStrategy {
                        override fun render(
                            renderingOrder: List<CanvasLayer>,
                            layerEntities: Iterable<EcsEntity>,
                            dirtyLayerEntities: Iterable<EcsEntity>
                        ) {
                            dirtyLayerEntities.forEach {
                                it.get<CanvasLayerComponent>().canvasLayer.apply { clear(); render(); }
                                it.untag<DirtyCanvasLayerComponent>()
                            }

                            PlatformAsyncs
                                .composite(renderingOrder.map(CanvasLayer::takeSnapshot))
                                .onSuccess { snapshots ->
                                    singleCanvasControl.context.clearRect(rect)
                                    snapshots.forEach { singleCanvasControl.context.drawImage(it, 0.0, 0.0) }
                                }
                        }
                    }
                )
            }

            override fun addLayer(name: String, group: LayerGroup): CanvasLayerComponent {
                val canvasLayer = CanvasLayer(singleCanvasControl.createCanvas(), name)
                myGroupedLayers.add(group, canvasLayer)
                return CanvasLayerComponent(canvasLayer)
            }

            override fun removeLayer(group: LayerGroup, canvasLayer: CanvasLayer) {
                myGroupedLayers.remove(group, canvasLayer)
            }

            override fun createLayersOrderComponent(): LayersOrderComponent {
                return LayersOrderComponent(myGroupedLayers)
            }
        }
    }

    private fun screenLayers(canvasControl: CanvasControl, componentManager: EcsComponentManager): LayerManager {
        return object : LayerManager {
            private val myGroupedLayers = GroupedLayers()

            override fun createLayerRenderingSystem(): LayersRenderingSystem {
                return LayersRenderingSystem(
                    componentManager,
                    object : RenderingStrategy {
                        override fun render(
                            renderingOrder: List<CanvasLayer>,
                            layerEntities: Iterable<EcsEntity>,
                            dirtyLayerEntities: Iterable<EcsEntity>
                        ) {
                            dirtyLayerEntities.forEach {
                                it.get<CanvasLayerComponent>().canvasLayer.apply { clear(); render(); }
                                it.untag<DirtyCanvasLayerComponent>()
                            }
                        }
                    }
                )
            }

            override fun addLayer(name: String, group: LayerGroup): CanvasLayerComponent {
                val canvas = canvasControl.createCanvas(canvasControl.size)


                val canvasLayer = CanvasLayer(canvas, name)
                myGroupedLayers.add(group, canvasLayer)

                canvasControl.addChild(myGroupedLayers.orderedLayers.indexOf(canvasLayer), canvas)

                return CanvasLayerComponent(canvasLayer)
            }

            override fun removeLayer(group: LayerGroup, canvasLayer: CanvasLayer) {
                canvasLayer.removeFrom(canvasControl)
                myGroupedLayers.remove(group, canvasLayer)
            }

            override fun createLayersOrderComponent(): LayersOrderComponent {
                return LayersOrderComponent(myGroupedLayers)
            }
        }
    }
}