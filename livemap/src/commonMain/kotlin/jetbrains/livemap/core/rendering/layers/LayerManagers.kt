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
                            renderingOrder: List<RenderLayer>,
                            layerEntities: Iterable<EcsEntity>,
                            dirtyLayerEntities: Iterable<EcsEntity>
                        ) {
                            singleCanvasControl.context.clearRect(rect)
                            renderingOrder.forEach(RenderLayer::render)
                            // Force render tasks to be added
                            layerEntities.forEach { it.tag(::DirtyRenderLayerComponent) }
                        }

                    }
                )
            }

            override fun createRenderLayerComponent(name: String, group: LayerGroup): RenderLayerComponent {
                val renderLayer = RenderLayer(singleCanvasControl.canvas, name)
                myGroupedLayers.add(group, renderLayer)
                return RenderLayerComponent(renderLayer)
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
                            renderingOrder: List<RenderLayer>,
                            layerEntities: Iterable<EcsEntity>,
                            dirtyLayerEntities: Iterable<EcsEntity>
                        ) {
                            dirtyLayerEntities.forEach {
                                it.get<RenderLayerComponent>().renderLayer.apply { clear(); render(); }
                                it.untag<DirtyRenderLayerComponent>()
                            }

                            PlatformAsyncs
                                .composite(renderingOrder.map(RenderLayer::takeSnapshot))
                                .onSuccess { snapshots ->
                                    singleCanvasControl.context.clearRect(rect)
                                    snapshots.forEach { singleCanvasControl.context.drawImage(it, 0.0, 0.0) }
                                }
                        }
                    }
                )
            }

            override fun createRenderLayerComponent(name: String, group: LayerGroup): RenderLayerComponent {
                val renderLayer = RenderLayer(singleCanvasControl.createCanvas(), name)
                myGroupedLayers.add(group, renderLayer)
                return RenderLayerComponent(renderLayer)
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
                            renderingOrder: List<RenderLayer>,
                            layerEntities: Iterable<EcsEntity>,
                            dirtyLayerEntities: Iterable<EcsEntity>
                        ) {
                            dirtyLayerEntities.forEach {
                                it.get<RenderLayerComponent>().renderLayer.apply { clear(); render(); }
                                it.untag<DirtyRenderLayerComponent>()
                            }
                        }
                    }
                )
            }

            override fun createRenderLayerComponent(name: String, group: LayerGroup): RenderLayerComponent {
                val canvas = canvasControl.createCanvas(canvasControl.size)
                canvasControl.addChild(canvas)

                val renderLayer = RenderLayer(canvas, name)
                myGroupedLayers.add(group, renderLayer)
                return RenderLayerComponent(renderLayer)
            }

            override fun createLayersOrderComponent(): LayersOrderComponent {
                return LayersOrderComponent(myGroupedLayers)
            }
        }
    }
}