package jetbrains.livemap.tiles

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.entities.placement.Components.ScreenDimensionComponent
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.tiles.Components.CellComponent
import jetbrains.livemap.tiles.Components.DebugDataComponent
import jetbrains.livemap.tiles.Components.DebugDataComponent.Companion.BIGGEST_LAYER
import jetbrains.livemap.tiles.Components.DebugDataComponent.Companion.CELL_DATA_SIZE
import jetbrains.livemap.tiles.Components.DebugDataComponent.Companion.LABEL_RENDER_TIME
import jetbrains.livemap.tiles.Components.DebugDataComponent.Companion.LABEL_SNAPSHOT_TIME
import jetbrains.livemap.tiles.Components.DebugDataComponent.Companion.LOADING_TIME
import jetbrains.livemap.tiles.Components.DebugDataComponent.Companion.PARSING_TIME
import jetbrains.livemap.tiles.Components.DebugDataComponent.Companion.WORLD_RENDER_TIME
import jetbrains.livemap.tiles.Components.DebugDataComponent.Companion.WORLD_SNAPSHOT_TIME

class DebugCellRenderer : Renderer {
    private var myOffset: Int = 0

    override fun render(entity: EcsEntity, ctx: Context2d) {
        val dimension = ScreenDimensionComponent.getDimension(entity)
        val debugData = DebugDataComponent.get(entity)

        myOffset = 0

        ctx.setFillColor(Color.RED.toCssColor())
        ctx.setStrokeColor(Color.RED.toCssColor())
        ctx.setLineWidth(2.0)
        ctx.setFont("12px serif")

        ctx.strokeRect(0.0, 0.0, dimension.x, dimension.y)

        drawNextLine(ctx, CellComponent.getCellKey(entity).toString())
        drawNextLines(
            ctx, debugData,
            CELL_DATA_SIZE,
            LOADING_TIME,
            PARSING_TIME,
            BIGGEST_LAYER,
            WORLD_RENDER_TIME,
            WORLD_SNAPSHOT_TIME,
            LABEL_RENDER_TIME,
            LABEL_SNAPSHOT_TIME
        )
    }

    private fun drawNextLine(ctx: Context2d, string: String) {
        myOffset += LINE_HEIGHT
        ctx.fillText(string, LINE_HEIGHT.toDouble(), myOffset.toDouble())
    }

    private fun drawNextLines(ctx: Context2d, debugData: DebugDataComponent, vararg keys: String) {
        for (key in keys) {
            drawNextLine(ctx, debugData.getLine(key))
        }
    }

    companion object {
        private const val LINE_HEIGHT = 20
    }
}