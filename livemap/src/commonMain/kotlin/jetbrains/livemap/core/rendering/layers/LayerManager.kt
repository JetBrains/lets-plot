package jetbrains.livemap.core.rendering.layers

interface LayerManager {
    fun createLayerRenderingSystem(): LayersRenderingSystem
    fun createRenderLayerComponent(name: String): RenderLayerComponent
    fun createLayersOrderComponent(): LayersOrderComponent
}