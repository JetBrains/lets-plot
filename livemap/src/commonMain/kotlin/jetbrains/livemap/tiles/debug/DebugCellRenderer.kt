/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles.debug

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.entities.placement.ScreenDimensionComponent
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.projections.ClientPoint
import jetbrains.livemap.projections.Coordinates.ZERO_CLIENT_POINT
import jetbrains.livemap.tiles.components.CellComponent
import jetbrains.livemap.tiles.components.DebugDataComponent
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.LINES_ORDER

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

        ctx.drawNextLines(entity.get(), LINES_ORDER)
    }

    private fun Context2d.strokeRect(origin: ClientPoint, dimension: ClientPoint) {
        strokeRect(origin.x, origin.y, dimension.x, dimension.y)
    }

    private fun Context2d.drawNextLine(string: String) {
        myOffset += LINE_HEIGHT
        fillText(string, LINE_HEIGHT, myOffset)
    }

    private fun Context2d.drawNextLines(debugData: DebugDataComponent, keys: List<String>) {
        keys.forEach { drawNextLine(it + ": " + debugData.get(it)) }
    }

    companion object {
        private const val LINE_WIDTH = 2.0
        private const val LINE_HEIGHT = 20.0
        private const val FONT_STYLE = "12px serif"
    }
}