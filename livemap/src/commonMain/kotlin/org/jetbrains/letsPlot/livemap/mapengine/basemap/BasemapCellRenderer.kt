/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap

import org.jetbrains.letsPlot.commons.intern.spatial.projectRect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper
import org.jetbrains.letsPlot.livemap.mapengine.Renderer
import org.jetbrains.letsPlot.livemap.mapengine.basemap.Tile.*
import org.jetbrains.letsPlot.livemap.mapengine.placement.ScreenDimensionComponent
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOriginComponent
import org.jetbrains.letsPlot.livemap.mapengine.translate
import org.jetbrains.letsPlot.livemap.mapengine.viewport.CellKey
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*

class BasemapCellRenderer : Renderer {
    private lateinit var myCellRect: Rect<org.jetbrains.letsPlot.livemap.Client>
    private lateinit var myCtx: Context2d

    override fun render(entity: EcsEntity, ctx: Context2d, renderHelper: RenderHelper) {
        val tile = entity.get<BasemapTileComponent>().tile ?: return
        ctx.translate(renderHelper.dimToScreen(entity.get<WorldOriginComponent>().origin))

        entity.get<ScreenDimensionComponent>()
            .dimension
            .run { Rect.XYWH(org.jetbrains.letsPlot.livemap.Client.ZERO_VEC, this) }
            .run { render(tile, this, ctx) }
    }

    internal fun render(tile: Tile, cellRect: Rect<org.jetbrains.letsPlot.livemap.Client>, ctx: Context2d) {
        myCellRect = cellRect
        myCtx = ctx
        renderTile(
            tile,
            CellKey(""),
            CellKey("")
        )
    }

    private fun renderTile(tile: Tile, srcCell: CellKey, dstCell: CellKey) {
        when (tile) {
            is SnapshotTile -> renderSnapshotTile(tile, srcCell, dstCell)
            is SubTile -> renderSubTile(tile, srcCell, dstCell)
            is CompositeTile -> renderCompositeTile(tile, srcCell, dstCell)
            is EmptyTile -> {}
            else -> error("Unsupported Tile class: ${Tile::class}")
        }
    }

    private fun renderSubTile(tile: SubTile, srcCell: CellKey, dstCell: CellKey) {
        renderTile(tile.tile, tile.subKey + srcCell, dstCell)
    }

    private fun renderCompositeTile(tile: CompositeTile, srcCell: CellKey, dstCell: CellKey) {
        tile.tiles.forEach { (tile, cell) -> renderTile(tile, srcCell, dstCell + cell) }
    }

    private fun renderSnapshotTile(tile: SnapshotTile, srcCell: CellKey, dstCell: CellKey) {
        val srcRect = srcCell.projectRect(myCellRect)
        val dstRect = dstCell.projectRect(myCellRect)
        myCtx.drawImage(
            tile.snapshot,
            srcRect.left,
            srcRect.top,
            srcRect.width,
            srcRect.height,
            dstRect.left,
            dstRect.top,
            dstRect.width + 1.0, // fix lines between tiles
            dstRect.height + 1.0 // fix lines between tiles
        )
    }

}