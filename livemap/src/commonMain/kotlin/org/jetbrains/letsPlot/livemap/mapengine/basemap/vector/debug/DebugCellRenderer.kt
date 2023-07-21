/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.debug

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.ClientPoint
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper
import org.jetbrains.letsPlot.livemap.mapengine.Renderer
import org.jetbrains.letsPlot.livemap.mapengine.basemap.BasemapCellComponent
import org.jetbrains.letsPlot.livemap.mapengine.basemap.DebugDataComponent
import org.jetbrains.letsPlot.livemap.mapengine.basemap.DebugDataComponent.Companion.LINES_ORDER
import org.jetbrains.letsPlot.livemap.mapengine.placement.ScreenDimensionComponent

class DebugCellRenderer : Renderer {
    private var myOffset: Double = 0.0

    override fun render(entity: EcsEntity, ctx: Context2d, renderHelper: RenderHelper) {
        val cellDimension = entity.get<ScreenDimensionComponent>().dimension

        myOffset = 0.0

        ctx.setFillStyle(Color.RED)
        ctx.setStrokeStyle(Color.RED)
        ctx.setLineWidth(LINE_WIDTH)
        ctx.setFont(FONT)

        ctx.strokeRect(org.jetbrains.letsPlot.livemap.Client.ZERO_VEC, cellDimension)

        ctx.drawNextLine(entity.get<BasemapCellComponent>().cellKey.toString())

        ctx.drawNextLines(entity.get(), LINES_ORDER)
    }

    private fun Context2d.strokeRect(origin: org.jetbrains.letsPlot.livemap.ClientPoint, dimension: org.jetbrains.letsPlot.livemap.ClientPoint) {
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