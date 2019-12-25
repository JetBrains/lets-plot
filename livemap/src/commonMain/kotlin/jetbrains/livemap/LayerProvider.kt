package jetbrains.livemap

import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.api.LayersBuilder
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.entities.rendering.TextMeasurer
import jetbrains.livemap.projections.MapProjection


interface LayerProvider {

    fun provide(
        componentManager: EcsComponentManager,
        layerManager: LayerManager,
        mapProjection: MapProjection,
        context2d: Context2d
    )

    class LayerProviderImpl(
        private val myDevParams: DevParams,
        private val myLayerConfigurator: LayersBuilder.() -> Unit
    ) : LayerProvider {

        override fun provide(
            componentManager: EcsComponentManager,
            layerManager: LayerManager,
            mapProjection: MapProjection,
            context2d: Context2d
        ) {
            LayersBuilder(
                componentManager,
                layerManager,
                mapProjection,
                myDevParams.isSet(DevParams.POINT_SCALING),
                TextMeasurer(context2d)
            ).apply(myLayerConfigurator)
        }
    }

    class EmptyLayerProvider() : LayerProvider {
        override fun provide(
            componentManager: EcsComponentManager,
            layerManager: LayerManager,
            mapProjection: MapProjection,
            context2d: Context2d
        ) {
            //
        }
    }
}