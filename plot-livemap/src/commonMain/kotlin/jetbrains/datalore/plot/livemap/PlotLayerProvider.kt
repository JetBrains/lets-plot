package jetbrains.datalore.plot.livemap

import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.DevParams
import jetbrains.livemap.LayerProvider
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.mapobjects.MapLayer
import jetbrains.livemap.mapobjects.MapLayerKind
import jetbrains.livemap.obj2entity.MapObject2Entity
import jetbrains.livemap.obj2entity.TextMeasurer
import jetbrains.livemap.projections.MapProjection

class PlotLayerProvider(
    private val myMapLayers: List<MapLayer>,
    private val myDevParams: DevParams
) : LayerProvider {
    override val layers: List<MapLayer>
        get() = myMapLayers

    override fun provide(
        componentManager: EcsComponentManager,
        layerManager: LayerManager,
        mapProjection: MapProjection,
        context2d: Context2d
    ) {
        val mapObject2Entity = MapObject2Entity(componentManager, layerManager, myDevParams, mapProjection)
        for (mapLayer in myMapLayers) {
            val kind = mapLayer.kind
            val mapObjects = mapLayer.mapObjects

            when(kind) {
                MapLayerKind.POINT -> mapObject2Entity.processPoint(mapObjects)
                MapLayerKind.PATH -> mapObject2Entity.processPath(mapObjects)
                MapLayerKind.POLYGON -> mapObject2Entity.processPolygon(mapObjects)
                MapLayerKind.BAR -> mapObject2Entity.processBar(mapObjects)
                MapLayerKind.PIE -> mapObject2Entity.processPie(mapObjects)
                MapLayerKind.H_LINE -> mapObject2Entity.processLine(mapObjects, true)
                MapLayerKind.V_LINE -> mapObject2Entity.processLine(mapObjects, false)
                MapLayerKind.TEXT -> mapObject2Entity.processText(
                    mapObjects,
                    TextMeasurer(context2d)
                )
                else -> error("")
            }
        }
    }

}