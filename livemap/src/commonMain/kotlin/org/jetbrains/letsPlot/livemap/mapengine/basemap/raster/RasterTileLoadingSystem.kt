/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap.raster

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.intern.spatial.projectOrigin
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Untyped
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.livemap.config.TILE_PIXEL_SIZE
import org.jetbrains.letsPlot.livemap.core.BusyStateComponent
import org.jetbrains.letsPlot.livemap.core.ecs.*
import org.jetbrains.letsPlot.livemap.core.layers.ParentLayerComponent
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
                    +BasemapCellComponent(cellKey)
                    +tileResponseComponent
                }

            myTileTransport.get(replacePlaceholders(cellKey, nextDomain())).onResult(
                successHandler = {
                    println("Raster tile downloaded: $cellKey, ${it.size} bytes")
                    tileResponseComponent.imageData = it
                },
                failureHandler = {
                    println("Raster tile download error: $cellKey, $it")
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

            val microThreads = getTileLayerEntities(cellKey).toList()
                .map { httpTileEntity ->
                    MicroTaskUtil.create {
                        when (response.errorCode) {
                            null -> drawImageTile(imageData, context)
                            else -> drawErrorTile(response.errorCode, context)
                        }.onSuccess { tile ->
                            println("Raster tile loaded: $cellKey")
                            runLaterBySystem(httpTileEntity) { theEntity ->
                                theEntity.remove<BusyStateComponent>()
                                theEntity.get<BasemapTileComponent>().also {
                                    it.nonCacheable = response.errorCode != null
                                    it.tile = tile
                                }
                                ParentLayerComponent.tagDirtyParentLayer(theEntity)
                            }
                        }
                    }
                }

            MicroTaskUtil.join(microThreads)
            entity.setMicroThread(1, MicroTaskUtil.join(microThreads))
        }

        @Suppress("ConvertLambdaToReference")
        downloadedEntities.forEach { it.remove<HttpTileResponseComponent>() }
    }

    private fun drawImageTile(imageData: ByteArray, context: LiveMapContext): Async<Tile> {
        return context.mapRenderContext.canvasProvider
            .decodePng(imageData)
            .map { imageSnapshot ->
                val tileCanvas = context.mapRenderContext.canvasProvider.createCanvas(TILE_PIXEL_DIMENSION)

                // Scale the image to fit the tile size, e.g.:
                // For DPI=2 and image 256x256, the resulting tile will be 512x512 pixels.
                // For DPI=1 and image 512x512, the resulting tile will be 256x256 pixels.
                tileCanvas.context2d.drawImage(
                    snapshot = imageSnapshot,
                    x = 0.0,
                    y = 0.0,
                    dw = TILE_PIXEL_SIZE,
                    dh = TILE_PIXEL_SIZE
                )

                Tile.SnapshotTile(tileCanvas.takeSnapshot(), context.mapRenderContext.pixelDensity)
            }
    }

    private fun drawErrorTile(
        errorCode: Throwable?,
        context: LiveMapContext
    ): Async<Tile> {
        val errorText = errorCode!!.message ?: "Unknown error"
        val tileCanvas = context.mapRenderContext.canvasProvider.createCanvas(TILE_PIXEL_DIMENSION)
        val tileCtx = tileCanvas.context2d
        val textDim = tileCtx.measureTextWidth(errorText)
        val x = if (textDim < TILE_PIXEL_SIZE) {
            TILE_PIXEL_SIZE / 2 - textDim / 2
        } else {
            4.0
        }
        tileCtx.setFont(Font())
        tileCtx.fillText(errorText, x, TILE_PIXEL_SIZE / 2)
        return Asyncs.constant(Tile.SnapshotTile(tileCanvas.takeSnapshot(), context.mapRenderContext.pixelDensity))
    }

    private fun getTileLayerEntities(cellKey: CellKey): Sequence<EcsEntity> {
        return getEntities2<BasemapCellComponent, KindComponent>()
            .filter {
                it.get<BasemapCellComponent>().cellKey == cellKey
                        && it.get<KindComponent>().layerKind == BasemapLayerKind.RASTER
            }
    }

    companion object {
        fun replacePlaceholders(cellKey: CellKey, domain: String): String {
            return 2.0.pow(cellKey.length)
                .let { Rect.XYWH<Untyped>(0.0, 0.0, it, it) }
                .let(cellKey::projectOrigin)
                .let {
                    domain
                        .replace("{z}", cellKey.length.toString(), ignoreCase = true)
                        .replace("{x}", it.x.roundToInt().toString(), ignoreCase = true)
                        .replace("{y}", it.y.roundToInt().toString(), ignoreCase = true)
                }
        }

        val TILE_PIXEL_DIMENSION = Vector(TILE_PIXEL_SIZE.toInt(), TILE_PIXEL_SIZE.toInt())
    }

    class HttpTileResponseComponent : EcsComponent {
        var imageData: ByteArray? = null
        var errorCode: Throwable? = null
    }
}
