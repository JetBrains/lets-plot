/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap.raster

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.intern.spatial.projectOrigin
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Untyped
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.livemap.config.TILE_PIXEL_SIZE
import org.jetbrains.letsPlot.livemap.core.ecs.*
import org.jetbrains.letsPlot.livemap.core.layers.ParentLayerComponent
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTask
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTaskUtil
import org.jetbrains.letsPlot.livemap.core.multitasking.setMicroThread
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext
import org.jetbrains.letsPlot.livemap.mapengine.basemap.*
import org.jetbrains.letsPlot.livemap.mapengine.viewport.CellKey
import kotlin.math.pow
import kotlin.math.roundToInt

class RasterTileLoadingSystem(
    private val myDomains: List<String>,
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {
    private var myIndex = 0
    private val myTileTransport: HttpTileTransport = HttpTileTransport()

    private fun nextDomain(): String {
        return myDomains[myIndex++].also {
            myIndex = myIndex % myDomains.size
        }
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        getSingleton<RequestTilesComponent>().requestTiles.forEach { cellKey ->
            val tileResponseComponent = HttpTileResponseComponent()

            createEntity("http_tile_$cellKey")
                .addComponents {
                    + BasemapCellComponent(cellKey)
                    + tileResponseComponent
                }

            myTileTransport.get(replacePlaceholders(cellKey, nextDomain())).onResult(
                successHandler = { tileResponseComponent.imageData = it },
                failureHandler = {
                    tileResponseComponent.imageData = ByteArray(0)
                    tileResponseComponent.errorCode = it
                }
            )
        }

        val downloadedEntities = ArrayList<EcsEntity>()
        for (entity in getEntities<HttpTileResponseComponent>()) {
            val response = entity.get<HttpTileResponseComponent>()
            val imageData = response.imageData ?: continue
            downloadedEntities.add(entity)
            val cellKey = entity.get<BasemapCellComponent>().cellKey

            val microThreads = ArrayList<MicroTask<Unit>>()
            getTileLayerEntities(cellKey).forEach { httpTileEntity ->
                microThreads.add(
                    MicroTaskUtil.create {
                        if (response.errorCode != null) {
                            val errorText = response.errorCode!!.message ?: "Unknown error"
                            val tileCanvas = context.mapRenderContext.canvasProvider.createCanvas(TILE_PIXEL_DIMENSION)
                            val tileCtx = tileCanvas.context2d
                            val textDim = tileCtx.measureTextWidth(errorText)
                            val x =
                                if (textDim < TILE_PIXEL_SIZE) {
                                    TILE_PIXEL_SIZE / 2 - textDim / 2
                                } else {
                                    4.0
                                }
                            tileCtx.setFont(Font())
                            tileCtx.fillText(errorText, x, TILE_PIXEL_SIZE / 2)
                            Asyncs.constant(tileCanvas.takeSnapshot())
                        } else {
                            context.mapRenderContext.canvasProvider.decodePng(imageData, TILE_PIXEL_DIMENSION)
                        }
                            .onSuccess { snapshot ->
                                runLaterBySystem(httpTileEntity) { theEntity ->
                                    theEntity.get<BasemapTileComponent>().apply {
                                        nonCacheable = response.errorCode != null
                                        tile = Tile.SnapshotTile(snapshot)
                                    }
                                    ParentLayerComponent.tagDirtyParentLayer(theEntity)
                                }
                        }
                    }
                )
            }

            MicroTaskUtil.join(microThreads)

            entity.setMicroThread(1, MicroTaskUtil.join(microThreads))
        }

        @Suppress("ConvertLambdaToReference")
        downloadedEntities.forEach { it.remove<HttpTileResponseComponent>() }
    }

    private fun getTileLayerEntities(cellKey: CellKey): Sequence<EcsEntity> {
        return getEntities2<BasemapCellComponent, KindComponent>()
            .filter { it.get<BasemapCellComponent>().cellKey == cellKey &&
            it.get<KindComponent>().layerKind == BasemapLayerKind.RASTER }
    }

    companion object {
        fun replacePlaceholders(cellKey: CellKey, domain: String): String {
            return 2.0.pow(cellKey.length)
                .let { Rect.XYWH<Untyped>(0.0, 0.0, it, it) }
                .let(cellKey::projectOrigin)
                .let {
                    domain
                        .replace("{z}", cellKey.length.toString(),  ignoreCase = true)
                        .replace("{x}", it.x.roundToInt().toString(), ignoreCase = true)
                        .replace("{y}", it.y.roundToInt().toString(), ignoreCase = true)
                }
        }
        val TILE_PIXEL_DIMENSION = Vector(TILE_PIXEL_SIZE.toInt(), TILE_PIXEL_SIZE.toInt())
    }

    class HttpTileResponseComponent: EcsComponent {
        var imageData: ByteArray? = null
        var errorCode: Throwable? = null
    }
}
