package jetbrains.livemap.tiles

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.projectionGeometry.GeoUtils.getTileRect
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.entities.placement.Components.ScreenDimensionComponent
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.projections.Coordinates.Companion.ZERO_CLIENT_POINT
import jetbrains.livemap.projections.newDoubleRectangle
import jetbrains.livemap.tiles.Tile.*
import jetbrains.livemap.tiles.components.TileComponent

class TileRenderer : Renderer {
    private lateinit var myCellRect: DoubleRectangle
    private lateinit var myCtx: Context2d

    override fun render(entity: EcsEntity, ctx: Context2d) {
        val tile = entity.get<TileComponent>().tile ?: return

        val dimension = ScreenDimensionComponent.getDimension(entity)
        render(tile, newDoubleRectangle(ZERO_CLIENT_POINT, dimension), ctx)
    }

    internal fun render(tile: Tile, cellRect: DoubleRectangle, ctx: Context2d) {
        myCellRect = cellRect
        myCtx = ctx
        renderTile(tile, "", "")
    }

    private fun renderTile(tile: Tile, srcKey: String, dstKey: String) {
        when (tile) {
            is SnapshotTile -> renderSnapshotTile(tile, srcKey, dstKey)
            is SubTile -> renderSubTile(tile, srcKey, dstKey)
            is CompositeTile -> renderCompositeTile(tile, srcKey, dstKey)
            is EmptyTile -> {}
            else -> error("Unsupported Tile class: ${Tile::class}")
        }
    }

    private fun renderSnapshotTile(tile: SnapshotTile, srcKey: String, dstKey: String) {
        val srcRect = getTileRect(myCellRect, srcKey)
        val dstRect = getTileRect(myCellRect, dstKey)
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

    private fun renderSubTile(tile: SubTile, srcKey: String, dstKey: String) {
        renderTile(tile.tile, tile.subKey + srcKey, dstKey)
    }

    private fun renderCompositeTile(tile: CompositeTile, srcKey: String, dstKey: String) {
        tile.tiles.forEach { subTile -> renderTile(subTile.first, srcKey, dstKey + subTile.second) }
    }
}