package jetbrains.livemap.tiles

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.entities.placement.ScreenDimensionComponent
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.projections.ClientPoint
import jetbrains.livemap.projections.Coordinates.Companion.ZERO_CLIENT_POINT
import jetbrains.livemap.tiles.components.CellComponent
import jetbrains.livemap.tiles.components.DebugDataComponent
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.BIGGEST_LAYER
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.CELL_DATA_SIZE
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.LABEL_RENDER_TIME
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.LABEL_SNAPSHOT_TIME
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.LOADING_TIME
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.PARSING_TIME
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.WORLD_RENDER_TIME
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.WORLD_SNAPSHOT_TIME

class DebugCellRenderer : Renderer {
    private var myOffset: Double = 0.0

    override fun render(entity: EcsEntity, ctx: Context2d) {
        val cellDimension = entity.get<ScreenDimensionComponent>().dimension

        myOffset = 0.0

        ctx.setFillStyle(Color.RED.toCssColor())
        ctx.setStrokeStyle(Color.RED.toCssColor())
        ctx.setLineWidth(LINE_WIDTH)
        ctx.setFont(FONT_STYLE)

        ctx.strokeRect(ZERO_CLIENT_POINT, cellDimension)

        ctx.drawNextLine(entity.get<CellComponent>().cellKey.toString())

        ctx.drawNextLines(entity.get(), LINES)
    }

    private fun Context2d.strokeRect(origin: ClientPoint, dimension: ClientPoint) {
        strokeRect(origin.x, origin.y, dimension.x, dimension.y)
    }

    private fun Context2d.drawNextLine(string: String) {
        myOffset += LINE_HEIGHT
        fillText(string, LINE_HEIGHT, myOffset)
    }

    private fun Context2d.drawNextLines(debugData: DebugDataComponent, keys: List<String>) {
        keys.forEach { drawNextLine(debugData.getLine(it)) }
    }

    companion object {
        private const val LINE_WIDTH = 2.0
        private const val LINE_HEIGHT = 20.0
        private const val FONT_STYLE = "12px serif"

        private val LINES = listOf(
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
}