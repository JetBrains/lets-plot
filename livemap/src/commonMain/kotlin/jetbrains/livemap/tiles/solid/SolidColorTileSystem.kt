/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles.solid

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.CanvasProvider
import jetbrains.livemap.LiveMapConstants.TILE_PIXEL_SIZE
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.cells.CellComponent
import jetbrains.livemap.cells.CellKey
import jetbrains.livemap.cells.CellLayerKind.RASTER
import jetbrains.livemap.cells.KindComponent
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.onEachEntity2
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.tiles.RequestTilesComponent
import jetbrains.livemap.tiles.Tile
import jetbrains.livemap.tiles.TileComponent
import kotlin.random.Random

class SolidColorTileSystem(
    private val tileFactory: (CellKey, CanvasProvider) -> Async<Canvas.Snapshot>,
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        getSingleton<RequestTilesComponent>().requestTiles.forEach { cellKey ->
            onEachEntity2<CellComponent, KindComponent>() { entity, cellComponent, kindComponnet ->
                if (kindComponnet.layerKind == RASTER && cellComponent.cellKey == cellKey) {
                    tileFactory(cellKey, context.mapRenderContext.canvasProvider)
                        .onSuccess { snapshot ->
                            runLaterBySystem(entity) {
                                it.get<TileComponent>().apply {
                                    tile = Tile.SnapshotTile(snapshot)
                                    nonCacheable = false
                                }
                                ParentLayerComponent.tagDirtyParentLayer(it)
                            }
                        }
                }
            }
        }
    }
}

private fun drawSolidColorTile(color: Color, canvasProvider: CanvasProvider): Async<Canvas.Snapshot> {
    var tileCanvas = canvasProvider.createCanvas(Vector(TILE_PIXEL_SIZE.toInt(), TILE_PIXEL_SIZE.toInt()))
    tileCanvas.context2d.apply {
        setFillStyle(color)
        fillRect(0.0, 0.0, TILE_PIXEL_SIZE.toDouble(), TILE_PIXEL_SIZE.toDouble())
    }

    return tileCanvas.takeSnapshot()
}

fun fixed(color: Color): (CellKey, CanvasProvider) -> Async<Canvas.Snapshot> {
    var tile: Async<Canvas.Snapshot>? = null
    return { _: CellKey, canvasProvider: CanvasProvider ->
        tile = tile ?: drawSolidColorTile(color, canvasProvider)
        tile!!
    }
}

fun chessBoard(black: Color, white: Color): (CellKey, CanvasProvider) -> Async<Canvas.Snapshot> {
    var blackTile: Async<Canvas.Snapshot>? = null
    var whiteTile: Async<Canvas.Snapshot>? = null

    return { cellKey: CellKey, canvasProvider: CanvasProvider ->
        blackTile = blackTile ?: drawSolidColorTile(black, canvasProvider)
        whiteTile = whiteTile ?: drawSolidColorTile(white, canvasProvider)

        when (cellKey.key.last()) {
            '0', '3' -> blackTile!!
            '1', '2' -> whiteTile!!
            else -> error("Invalid QuadKey component. Expected to be 0, 1, 2 or 3 but was ${cellKey.key.last()}. QuadKey: ${cellKey.key}")
        }
    }
}

fun random(): (CellKey, CanvasProvider) -> Async<Canvas.Snapshot> {
    return { _: CellKey, canvasProvider: CanvasProvider ->
        val color = Color(Random.nextInt(0, 256), Random.nextInt(0, 256), Random.nextInt(0, 256))
        drawSolidColorTile(color, canvasProvider)
    }
}
