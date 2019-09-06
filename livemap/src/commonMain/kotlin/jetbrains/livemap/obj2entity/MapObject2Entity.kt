package jetbrains.livemap.obj2entity

import jetbrains.datalore.maps.livemap.mapObjects2Entity.MapPathProcessor
import jetbrains.livemap.DevParams
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.mapobjects.MapObject
import jetbrains.livemap.projections.MapProjection


class MapObject2Entity(
    private val myComponentManager: EcsComponentManager,
    private val myLayerManager: LayerManager,
    private val myDevParams: DevParams,
    private val myMapProjection: MapProjection
) {

    fun processPoint(mapObjects: List<MapObject>) {
        MapPointProcessor(myComponentManager, myLayerManager, myDevParams, myMapProjection).process(mapObjects)
    }

    fun processPath(mapObjects: List<MapObject>) {
        MapPathProcessor(myComponentManager, myLayerManager, myMapProjection).process(mapObjects)
    }

    /*
    fun processPolygon(mapObjects: List<MapObject>) {
        MapPolygonProcessor(myComponentManager, myLayerManager).process(mapObjects)
    }

    fun processBar(mapObjects: List<MapObject>) {
        MapBarProcessor(myComponentManager, myLayerManager).process(mapObjects)
    }

    fun processPie(mapObjects: List<MapObject>) {
        MapPieProcessor(myComponentManager, myLayerManager).process(mapObjects)
    }

    fun processLine(mapObjects: List<MapObject>, horizontal: Boolean, mapRect: DoubleRectangle) {
        MapLineProcessor(myComponentManager, myLayerManager, mapRect).process(mapObjects, horizontal)
    }

    fun processText(mapObjects: List<MapObject>, textMeasurer: TextMeasurer) {
        MapTextProcessor(myComponentManager, myLayerManager, textMeasurer).process(mapObjects)
    }
    */
}
