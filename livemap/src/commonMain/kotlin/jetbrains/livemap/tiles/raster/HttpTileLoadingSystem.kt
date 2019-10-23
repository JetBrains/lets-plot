package jetbrains.livemap.tiles.raster

import jetbrains.datalore.base.projectionGeometry.Generic
import jetbrains.datalore.base.projectionGeometry.GeoUtils
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.gis.tileprotocol.http.TileTransport
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.*
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.core.multitasking.MicroTaskUtil
import jetbrains.livemap.core.multitasking.setMicroThread
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.projections.CellKey
import jetbrains.livemap.tiles.Tile
import jetbrains.livemap.tiles.vector.TileLoadingSystem
import jetbrains.livemap.tiles.components.*
import kotlin.math.pow
import kotlin.math.roundToInt

class HttpTileLoadingSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {
    private val myTileTransport: TileTransport = TileTransport("localhost", null, "")

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        getSingletonComponent<RequestTilesComponent>().requestTiles.forEach { cellKey ->
            val tileResponseComponent = HttpTileResponseComponent()

            createEntity("http_tile_$cellKey")
                .addComponents {
                    + CellComponent(cellKey)
                    + tileResponseComponent
                }

            myTileTransport.get(getZXY(cellKey)).onResult(
                { tileResponseComponent.imageData = it },
                { tileResponseComponent.imageData = ByteArray(0) }
            )
        }

        val downloadedEntities = ArrayList<EcsEntity>()
        for (entity in getEntities<HttpTileResponseComponent>()) {
            val imageData = entity.get<HttpTileResponseComponent>().imageData ?: continue
            downloadedEntities.add(entity)
            val cellKey = entity.get<CellComponent>().cellKey

            val microThreads = ArrayList<MicroTask<Unit>>()
            getTileLayerEntities(cellKey).forEach { httpTileEntity ->
                microThreads.add(
                    MicroTaskUtil.create {
                        context.mapRenderContext.canvasProvider.createSnapshot(imageData).onSuccess { snapshot ->

                            runLaterBySystem(httpTileEntity) { theEntity ->
                                theEntity.get<TileComponent>().tile = Tile.SnapshotTile(snapshot)
                                ParentLayerComponent.tagDirtyParentLayer(theEntity)
                                println(cellKey)
                            }
                        }
                    }
                )
            }

            MicroTaskUtil.join(microThreads)

            entity.setMicroThread(1, MicroTaskUtil.join(microThreads))
        }

        downloadedEntities.forEach { it.remove<HttpTileResponseComponent>() }
    }

    private fun getTileLayerEntities(cellKey: CellKey): Sequence<EcsEntity> {
        return getEntities(TileLoadingSystem.CELL_COMPONENT_LIST)
            .filter {
                it.get<CellComponent>().cellKey == cellKey
                        && it.get<KindComponent>().layerKind == CellLayerKind.HTTP
            }
    }

    companion object {
        fun getZXY(cellKey: CellKey): String {
            return 2.0.pow(cellKey.length)
                .let { Rect<Generic>(0.0, 0.0, it, it) }
                .let { GeoUtils.getTileOrigin(it, cellKey.key) }
                .let { "/${cellKey.length}/${it.x.roundToInt()}/${it.y.roundToInt()}.png" }
        }
    }

    private class HttpTileResponseComponent: EcsComponent {
        var imageData: ByteArray? = null
    }
}