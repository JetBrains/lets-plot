/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.text

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.chart.TextSpecComponent
import org.jetbrains.letsPlot.livemap.chart.changeAlphaWithMin
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper
import org.jetbrains.letsPlot.livemap.mapengine.Renderer
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOriginComponent
import org.jetbrains.letsPlot.livemap.mapengine.translate

class TextRenderer : Renderer {
    override fun render(entity: EcsEntity, ctx: Context2d, renderHelper: RenderHelper) {
        val chartElementComponent = entity.get<ChartElementComponent>()
        val textSpec = entity.get<TextSpecComponent>().textSpec

        val textPosition: Vec<org.jetbrains.letsPlot.livemap.Client>

        ctx.translate(renderHelper.dimToScreen(entity.get<WorldOriginComponent>().origin))
        ctx.rotate(textSpec.angle)

        if (textSpec.drawBorder) {
            val rectangle = textSpec.rectangle
            drawRoundedRectangle(rectangle, textSpec.labelRadius * rectangle.height, ctx)

            if (chartElementComponent.fillColor != null) {
                ctx.setFillStyle(
                    changeAlphaWithMin(chartElementComponent.fillColor!!, chartElementComponent.scalingAlphaValue)
                )
                ctx.fill()
            }
            if (chartElementComponent.strokeColor != null && textSpec.labelSize != 0.0) {
                ctx.setStrokeStyle(chartElementComponent.strokeColor)
                ctx.setLineWidth(textSpec.labelSize)
                ctx.stroke()
            }

            val xPosition = when (textSpec.hjust) {
                0.0 -> textSpec.padding
                1.0 -> -textSpec.padding
                else -> 0.0
            }
            textPosition = explicitVec(
                xPosition,
                rectangle.origin.y + textSpec.padding + textSpec.font.fontSize * 0.8 // top-align the first line
            )
        } else {
            val yPosition = with(textSpec) {
                when (vjust) {
                    1.0 -> font.fontSize * 0.7
                    0.0 -> -textSize.y + font.fontSize
                    else -> -textSize.y / 2 + font.fontSize * 0.8
                }
            }
            textPosition = explicitVec(0.0, yPosition)
        }

        ctx.setFont(textSpec.font)
        ctx.setFillStyle(chartElementComponent.strokeColor)

        ctx.setTextAlign(textSpec.textAlign)
        textSpec.lines.forEachIndexed { index, line ->
            ctx.fillText(line, textPosition.x, textPosition.y + textSpec.lineHeight * index)
        }
    }

    private fun drawRoundedRectangle(rect: DoubleRectangle, radius: Double, ctx: Context2d) {
        ctx.apply {
            beginPath()
            with(rect) {
                // Ensure normal radius
                val r = minOf(radius, rect.width / 2, rect.height / 2)

                moveTo(right - r, bottom)
                bezierCurveTo(
                    right - r, bottom,
                    right, bottom,
                    right, bottom - r
                )

                lineTo(right, top + r)
                bezierCurveTo(
                    right, top + r,
                    right, top,
                    right - r, top
                )

                lineTo(left + r, top)
                bezierCurveTo(
                    left + r, top,
                    left, top,
                    left, top + r
                )

                lineTo(left, bottom - r)
                bezierCurveTo(
                    left, bottom - r,
                    left, bottom,
                    left + r, bottom
                )
            }
            closePath()
        }
    }
}