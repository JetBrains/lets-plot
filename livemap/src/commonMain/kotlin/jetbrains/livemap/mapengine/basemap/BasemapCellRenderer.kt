/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.basemap

import jetbrains.datalore.base.spatial.projectRect
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.Client
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.Renderer
import jetbrains.livemap.mapengine.basemap.Tile.*
import jetbrains.livemap.mapengine.placement.ScreenDimensionComponent
import jetbrains.livemap.mapengine.viewport.CellKey
import jetbrains.livemap.mapengine.viewport.Viewport

class BasemapCellRenderer : Renderer {
    private lateinit var myCellRect: Rect<Client>
    private lateinit var myCtx: Context2d

    override fun render(entity: EcsEntity, ctx: Context2d, viewport: Viewport) {
        val tile = entity.get<BasemapTileComponent>().tile ?: return

        entity.get<ScreenDimensionComponent>()
            .dimension
            .run { Rect.XYWH(Client.ZERO_VEC, this) }
            .run { render(tile, this, ctx) }
    }

    internal fun render(tile: Tile, cellRect: Rect<Client>, ctx: Context2d) {
        myCellRect = cellRect
        myCtx = ctx
        renderTile(tile,
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