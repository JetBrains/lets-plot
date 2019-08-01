package jetbrains.livemap.core.rendering.layers

import jetbrains.datalore.base.async.PlatformAsyncs
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.base.canvas.SingleCanvasControl
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
            private val myOrderedLayers = ArrayList<RenderLayer>()

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
                            renderingOrder.forEach {it.render()}
                            // Force render tasks to be added
                            layerEntities.forEach { DirtyRenderLayerComponent.tag(it) }
                        }

                    }
                )
            }

            override fun createRenderLayerComponent(name: String): RenderLayerComponent {
                val renderLayer = RenderLayer(singleCanvasControl.canvas, name)
                myOrderedLayers.add(renderLayer)
                return RenderLayerComponent(renderLayer)
            }

            override fun createLayersOrderComponent(): LayersOrderComponent {
                return LayersOrderComponent(myOrderedLayers)
            }
        }
    }

    private fun offscreenLayers(canvasControl: CanvasControl, componentManager: EcsComponentManager): LayerManager {
        val singleCanvasControl = SingleCanvasControl(canvasControl)
        val rect = DoubleRectangle(DoubleVector.ZERO, canvasControl.size.toDoubleVector())

        return object : LayerManager {
            private val myOrderedLayers = ArrayList<RenderLayer>()

            override fun createLayerRenderingSystem(): LayersRenderingSystem {
                return LayersRenderingSystem(
                    componentManager,
                    object : RenderingStrategy {
                        override fun render(
                            renderingOrder: List<RenderLayer>,
                            layerEntities: Iterable<EcsEntity>,
                            dirtyLayerEntities: Iterable<EcsEntity>
                        ) {
                            dirtyLayerEntities.forEach { dirtyLayer ->
                                val renderLayer = RenderLayerComponent.getRenderLayer(dirtyLayer)
                                renderLayer.clear()
                                renderLayer.render()
                                DirtyRenderLayerComponent.untag(dirtyLayer)
                            }

                            PlatformAsyncs.composite(
                                renderingOrder.map { it.takeSnapshot() }
                            ).onSuccess { snapshots ->
                                singleCanvasControl.context.clearRect(rect)
                                snapshots.forEach { singleCanvasControl.context.drawImage(it, 0.0, 0.0) }
                            }
                        }
                    }
                )
            }

            override fun createRenderLayerComponent(name: String): RenderLayerComponent {
                val renderLayer = RenderLayer(singleCanvasControl.createCanvas(), name)
                myOrderedLayers.add(renderLayer)
                return RenderLayerComponent(renderLayer)
            }

            override fun createLayersOrderComponent(): LayersOrderComponent {
                return LayersOrderComponent(myOrderedLayers)
            }
        }
    }

    private fun screenLayers(canvasControl: CanvasControl, componentManager: EcsComponentManager): LayerManager {
        return object : LayerManager {
            private val myOrderedLayers = ArrayList<RenderLayer>()

            override fun createLayerRenderingSystem(): LayersRenderingSystem {
                return LayersRenderingSystem(
                    componentManager,
                    object : RenderingStrategy {
                        override fun render(
                            renderingOrder: List<RenderLayer>,
                            layerEntities: Iterable<EcsEntity>,
                            dirtyLayerEntities: Iterable<EcsEntity>
                        ) {
                            dirtyLayerEntities.forEach { entity ->
                                val renderLayer = RenderLayerComponent.getRenderLayer(entity)
                                renderLayer.clear()
                                renderLayer.render()
                                DirtyRenderLayerComponent.untag(entity)
                            }
                        }
                    }
                )
            }

            override fun createRenderLayerComponent(name: String): RenderLayerComponent {
                val canvas = canvasControl.createCanvas(canvasControl.size)
                canvasControl.addChild(canvas)

                val renderLayer = RenderLayer(canvas, name)
                myOrderedLayers.add(renderLayer)
                return RenderLayerComponent(renderLayer)
            }

            override fun createLayersOrderComponent(): LayersOrderComponent {
                return LayersOrderComponent(myOrderedLayers)
            }
        }
    }
}