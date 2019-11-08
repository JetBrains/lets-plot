package jetbrains.livemap

import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.api.LayersBuilder
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.mapobjects.MapLayer
import jetbrains.livemap.obj2entity.TextMeasurer
import jetbrains.livemap.projections.MapProjection


interface LayerProvider {
    val layers: List<MapLayer> //todo: temporary for MapDataGeocodingHelper

    fun provide(
        componentManager: EcsComponentManager,
        layerManager: LayerManager,
        mapProjection: MapProjection,
        context2d: Context2d
    )

    class LayerProviderImpl(
        private val myDevParams: DevParams,
        private val myBlock: LayersBuilder.() -> Unit
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
                myDevParams,
                TextMeasurer(context2d)
            ).apply(myBlock)
        }

        override val layers: List<MapLayer> = emptyList()
    }
}