/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.basemap.vector.debug

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.datalore.vis.canvas.Font
import jetbrains.livemap.Client
import jetbrains.livemap.ClientPoint
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.Renderer
import jetbrains.livemap.mapengine.basemap.BasemapCellComponent
import jetbrains.livemap.mapengine.basemap.DebugDataComponent
import jetbrains.livemap.mapengine.basemap.DebugDataComponent.Companion.LINES_ORDER
import jetbrains.livemap.mapengine.placement.ScreenDimensionComponent

class DebugCellRenderer : Renderer {
    private var myOffset: Double = 0.0

    override fun render(entity: EcsEntity, ctx: Context2d) {
        val cellDimension = entity.get<ScreenDimensionComponent>().dimension

        myOffset = 0.0

        ctx.setFillStyle(Color.RED)
        ctx.setStrokeStyle(Color.RED)
        ctx.setLineWidth(LINE_WIDTH)
        ctx.setFont(FONT)

        ctx.strokeRect(Client.ZERO_VEC, cellDimension)

        ctx.drawNextLine(entity.get<BasemapCellComponent>().cellKey.toString())

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
        private val FONT = Font(fontSize = 12.0)
    }
}