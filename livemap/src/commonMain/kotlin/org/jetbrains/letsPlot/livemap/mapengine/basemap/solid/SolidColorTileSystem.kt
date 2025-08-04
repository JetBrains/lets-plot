/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap.solid

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.livemap.config.TILE_PIXEL_SIZE
import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.onEachEntity
import org.jetbrains.letsPlot.livemap.core.layers.ParentLayerComponent
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext
import org.jetbrains.letsPlot.livemap.mapengine.basemap.BasemapCellComponent
import org.jetbrains.letsPlot.livemap.mapengine.basemap.BasemapTileComponent
import org.jetbrains.letsPlot.livemap.mapengine.basemap.RequestTilesComponent
import org.jetbrains.letsPlot.livemap.mapengine.basemap.Tile
import org.jetbrains.letsPlot.livemap.mapengine.viewport.CellKey
import kotlin.random.Random

class SolidColorTileSystem(
    private val tileFactory: (CellKey, CanvasPeer) -> Canvas.Snapshot,
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        getSingleton<RequestTilesComponent>().requestTiles.forEach { cellKey ->
            onEachEntity<BasemapCellComponent>() { entity, cellComponent ->
                if (cellComponent.cellKey == cellKey) {
                    val snapshot = tileFactory(cellKey, context.mapRenderContext.canvasPeer)
                    runLaterBySystem(entity) {
                        it.get<BasemapTileComponent>().apply {
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

private fun drawSolidColorTile(color: Color, canvasPeer: CanvasPeer): Canvas.Snapshot {
    val tileCanvas = canvasPeer.createCanvas(Vector(TILE_PIXEL_SIZE.toInt(), TILE_PIXEL_SIZE.toInt()))
    tileCanvas.context2d.apply {
        setFillStyle(color)
        fillRect(0.0, 0.0, TILE_PIXEL_SIZE, TILE_PIXEL_SIZE)
    }

    return tileCanvas.takeSnapshot()
}

fun fixed(color: Color): (CellKey, CanvasPeer) -> Canvas.Snapshot {
    var tile: Canvas.Snapshot? = null
    return { _: CellKey, canvasPeer: CanvasPeer ->
        tile = tile ?: drawSolidColorTile(color, canvasPeer)
        tile!!
    }
}

fun chessBoard(black: Color, white: Color): (CellKey, CanvasPeer) -> Canvas.Snapshot {
    var tile: Canvas.Snapshot? = null

    fun drawChessQuad(canvasPeer: CanvasPeer): Canvas.Snapshot {

        val tileCanvas = canvasPeer.createCanvas(Vector(TILE_PIXEL_SIZE.toInt(), TILE_PIXEL_SIZE.toInt()))
        val centerX = TILE_PIXEL_SIZE / 2
        val centerY = TILE_PIXEL_SIZE / 2
        tileCanvas.context2d.apply {
            setFillStyle(black)
            fillRect(0.0, 0.0, centerX, centerY)
            fillRect(centerX, centerY, centerX, centerY)

            setFillStyle(white)
            fillRect(centerX, 0.0, centerX, centerY)
            fillRect(0.0, centerY, centerX, centerY)
        }

        return tileCanvas.takeSnapshot()
    }

    return { _: CellKey, canvasPeer: CanvasPeer ->
        tile = tile ?: drawChessQuad(canvasPeer)
        tile!!
    }
}

fun random(): (CellKey, CanvasPeer) -> Canvas.Snapshot {
    return { _: CellKey, canvasPeer: CanvasPeer ->
        val color = Color(Random.nextInt(0, 256), Random.nextInt(0, 256), Random.nextInt(0, 256))
        drawSolidColorTile(color, canvasPeer)
    }
}
