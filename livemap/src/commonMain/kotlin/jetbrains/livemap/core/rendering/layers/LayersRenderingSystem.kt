package jetbrains.livemap.core.rendering.layers

import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsContext
import jetbrains.livemap.core.ecs.EcsEntity

class LayersRenderingSystem internal constructor(
    componentManager: EcsComponentManager,
    private val myRenderingStrategy: RenderingStrategy
) : AbstractSystem<EcsContext>(componentManager) {
    private val myDirtyLayers = ArrayList<Int>()

    val dirtyLayers: List<Int>
        get() = myDirtyLayers

    override fun updateImpl(context: EcsContext, dt: Double) {
        val orderedRenderLayers = getSingletonComponent(LayersOrderComponent::class).renderLayers

        val layerEntities = getEntities(RenderLayerComponent::class)
        val dirtyEntities = getEntities(DirtyRenderLayerComponent::class)

        myDirtyLayers.clear()
        dirtyEntities.forEach { myDirtyLayers.add(it.id) }

        myRenderingStrategy.render(orderedRenderLayers, layerEntities, dirtyEntities)
    }

    interface RenderingStrategy {
        fun render(
            renderingOrder: List<RenderLayer>,
            layerEntities: Iterable<EcsEntity>,
            dirtyLayerEntities: Iterable<EcsEntity>
        )
    }
}