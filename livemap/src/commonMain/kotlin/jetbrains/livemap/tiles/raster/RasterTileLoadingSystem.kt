/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles.raster

import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.spatial.projectOrigin
import jetbrains.datalore.base.typedGeometry.Generic
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.gis.tileprotocol.http.HttpTileTransport
import jetbrains.livemap.LiveMapConstants.TILE_PIXEL_SIZE
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.cells.CellComponent
import jetbrains.livemap.cells.CellKey
import jetbrains.livemap.cells.CellLayerKind
import jetbrains.livemap.cells.KindComponent
import jetbrains.livemap.core.ecs.*
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.core.multitasking.MicroTaskUtil
import jetbrains.livemap.core.multitasking.setMicroThread
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.tiles.RequestTilesComponent
import jetbrains.livemap.tiles.Tile
import jetbrains.livemap.tiles.TileComponent
import jetbrains.livemap.tiles.vector.TileLoadingSystem
import kotlin.math.pow
import kotlin.math.roundToInt

class RasterTileLoadingSystem(
    private val myRequestFormat: String,
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {
    private val myTileTransport: HttpTileTransport = HttpTileTransport()

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        getSingleton<RequestTilesComponent>().requestTiles.forEach { cellKey ->
            val tileResponseComponent = HttpTileResponseComponent()

            createEntity("http_tile_$cellKey")
                .addComponents {
                    + CellComponent(cellKey)
                    + tileResponseComponent
                }

            myTileTransport.get(getZXY(cellKey, myRequestFormat)).onResult(
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
                        context.mapRenderContext.canvasProvider
                            .createSnapshot(imageData, Vector(TILE_PIXEL_SIZE.toInt(), TILE_PIXEL_SIZE.toInt()))
                            .onSuccess { snapshot ->

                                runLaterBySystem(httpTileEntity) { theEntity ->
                                    theEntity.get<TileComponent>().tile = Tile.SnapshotTile(snapshot)
                                    ParentLayerComponent.tagDirtyParentLayer(theEntity)
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
                        && it.get<KindComponent>().layerKind == CellLayerKind.RASTER
            }
    }

    companion object {
        fun getZXY(cellKey: CellKey, format: String): String {
            return 2.0.pow(cellKey.length)
                .let { Rect<Generic>(0.0, 0.0, it, it) }
                .let { cellKey.projectOrigin(it) }
                .let {
                    format
                        .replace("{z}", cellKey.length.toString(), false)
                        .replace("{x}", it.x.roundToInt().toString(), false)
                        .replace("{y}", it.y.roundToInt().toString(), false)
                }
        }
    }

    class HttpTileResponseComponent: EcsComponent {
        var imageData: ByteArray? = null
    }
}