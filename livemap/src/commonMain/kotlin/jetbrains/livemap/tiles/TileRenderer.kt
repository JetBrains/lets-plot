/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles

import jetbrains.datalore.base.spatial.projectRect
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.entities.placement.ScreenDimensionComponent
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.projections.Client
import jetbrains.livemap.projections.Coordinates.ZERO_CLIENT_POINT
import jetbrains.livemap.tiles.Tile.*
import jetbrains.livemap.tiles.components.TileComponent

class TileRenderer : Renderer {
    private lateinit var myCellRect: Rect<Client>
    private lateinit var myCtx: Context2d

    override fun render(entity: EcsEntity, ctx: Context2d) {
        val tile = entity.get<TileComponent>().tile ?: return

        entity.get<ScreenDimensionComponent>()
            .dimension
            .run { Rect(ZERO_CLIENT_POINT, this) }
            .run { render(tile, this, ctx) }
    }

    internal fun render(tile: Tile, cellRect: Rect<Client>, ctx: Context2d) {
        myCellRect = cellRect
        myCtx = ctx
        renderTile(tile, CellKey(""), CellKey(""))
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
            dstRect.width,
            dstRect.height
        )
    }

}