/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.point

import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.chart.PointComponent
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper
import org.jetbrains.letsPlot.livemap.mapengine.Renderer
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOriginComponent
import org.jetbrains.letsPlot.livemap.mapengine.translate
import kotlin.math.PI
import kotlin.math.sqrt

class PointRenderer(
    private val shape: Int
) : Renderer {

    override fun render(entity: EcsEntity, ctx: Context2d, renderHelper: RenderHelper) {
        val chartElement = entity.get<ChartElementComponent>()
        val pointData = entity.get<PointComponent>()

        ctx.translate(renderHelper.dimToScreen(entity.get<WorldOriginComponent>().origin))

        ctx.beginPath()
        drawMarker(
            ctx = ctx,
            radius = pointData.scaledRadius(chartElement.scalingSizeFactor),
            stroke = chartElement.scaledStrokeWidth(),
            shape = shape
        )
        if (chartElement.fillColor != null) {
            ctx.setFillStyle(chartElement.scaledFillColor())
            ctx.fill()
        }
        if (chartElement.strokeColor != null && chartElement.scaledStrokeWidth() > 0.0) {
            ctx.setStrokeStyle(chartElement.scaledStrokeColor())
            ctx.setLineWidth(chartElement.scaledStrokeWidth())
            ctx.stroke()
        }
    }
    private fun drawMarker(ctx: Context2d, radius: Double, stroke: Double, shape: Int) {
        when (shape) {
            0 -> square(ctx, radius)
            1 -> circle(ctx, radius)
            2 -> triangle(ctx, radius, stroke)
            3 -> plus(ctx, radius)
            4 -> cross(ctx, radius / sqrt(2.0))
            5 -> diamond(ctx, radius)
            6 -> triangle(ctx, radius, stroke, pointingUp = false)
            7 -> {
                square(ctx, radius)
                cross(ctx, radius)
            }
            8 -> {
                plus(ctx, radius)
                cross(ctx, radius / sqrt(2.0))
            }
            9 -> {
                diamond(ctx, radius)
                plus(ctx, radius)
            }
            10 -> {
                circle(ctx, radius)
                plus(ctx, radius)
            }
            11 -> {
                triangle(ctx, radius, stroke, pointingUp = true, pinnedToCentroid = true)
                triangle(ctx, radius, stroke, pointingUp = false, pinnedToCentroid = true)
            }
            12 -> {
                square(ctx, radius)
                plus(ctx, radius)
            }
            13 -> {
                circle(ctx, radius)
                cross(ctx, radius / sqrt(2.0))
            }
            14 -> squareTriangle(ctx, radius, stroke)
            15 -> square(ctx, radius)
            16 -> circle(ctx, radius)
            17 -> triangle(ctx, radius, 1.0)
            18 -> diamond(ctx, radius)
            19 -> circle(ctx, radius)
            20 -> circle(ctx, radius)
            21 -> circle(ctx, radius)
            22 -> square(ctx, radius)
            23 -> diamond(ctx, radius)
            24 -> triangle(ctx, radius, stroke)
            25 -> triangle(ctx, radius, stroke, pointingUp = false)
            else -> throw IllegalStateException("Unknown point shape")
        }
    }

    private fun circle(ctx: Context2d, r: Double) {
        ctx.arc(0.0, 0.0, r, 0.0, 2 * PI)
    }

    private fun square(ctx: Context2d, r: Double) {
        ctx.moveTo(-r, -r)
        ctx.lineTo(r, -r)
        ctx.lineTo(r, r)
        ctx.lineTo(-r, r)
        ctx.closePath()
    }

    private fun squareTriangle(ctx: Context2d, r: Double, stroke: Double) {
        val outerSize = 2 * r + stroke
        val triangleHeight = outerSize - stroke / 2 - sqrt(5.0) * stroke / 2
        ctx.moveTo(-triangleHeight / 2, r)
        ctx.lineTo(0.0, r - triangleHeight)
        ctx.lineTo(triangleHeight / 2, r)
        ctx.lineTo(-r, r)
        ctx.lineTo(-r, -r)
        ctx.lineTo(r, -r)
        ctx.lineTo(r, r)
        ctx.closePath()
    }

    private fun triangle(ctx: Context2d, r: Double, stroke: Double, pointingUp: Boolean = true, pinnedToCentroid: Boolean = false) {
        val outerHeight = 2 * r + stroke
        val height = outerHeight - 3.0 * stroke / 2.0
        val side = 2.0 * height / sqrt(3.0)
        val distanceToBase = (outerHeight - stroke) / 2.0
        val distanceToPeak = height - distanceToBase
        val pointingCoeff = if (pointingUp)
            1.0
        else
            -1.0
        val centroidOffset = if (pinnedToCentroid)
            height / 6.0 + stroke / 4.0
        else
            0.0

        ctx.moveTo(0.0, -pointingCoeff * (distanceToPeak + centroidOffset))
        ctx.lineTo(side / 2.0, pointingCoeff * (distanceToBase - centroidOffset))
        ctx.lineTo(-side / 2.0, pointingCoeff * (distanceToBase - centroidOffset))
        ctx.lineTo(0.0, -pointingCoeff * (distanceToPeak + centroidOffset))
        ctx.closePath()
    }

    internal fun plus(ctx: Context2d, r: Double) {
        ctx.moveTo(0.0, -r)
        ctx.lineTo(0.0, r)
        ctx.moveTo(-r, 0.0)
        ctx.lineTo(r, 0.0)
    }

    private fun cross(ctx: Context2d, r: Double) {
        ctx.moveTo(-r, -r)
        ctx.lineTo(r, r)
        ctx.moveTo(-r, r)
        ctx.lineTo(r, -r)
    }

    private fun diamond(ctx: Context2d, r: Double) {
        ctx.moveTo(0.0, -r)
        ctx.lineTo(r, 0.0)
        ctx.lineTo(0.0, r)
        ctx.lineTo(-r, 0.0)
        ctx.closePath()
    }

}