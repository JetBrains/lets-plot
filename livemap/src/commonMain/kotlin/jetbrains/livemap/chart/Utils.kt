/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sqrt

object Utils {

    internal fun drawPath(ctx: Context2d, radius: Double, stroke: Double, shape: Int) {
        when (shape) {
            0 -> square(ctx, radius)
            1 -> circle(ctx, radius)
            2 -> triangleUp(ctx, radius)
            3 -> plus(ctx, radius)
            4 -> cross(ctx, radius)
            5 -> diamond(ctx, radius)
            6 -> triangleDown(ctx, radius)
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
                triangleUp(ctx, radius)
                triangleDown(ctx, radius)
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
            17 -> triangleUp(ctx, radius)
            18 -> diamond(ctx, radius)
            19 -> circle(ctx, radius)
            20 -> circle(ctx, radius)
            21 -> circle(ctx, radius)
            22 -> square(ctx, radius)
            23 -> diamond(ctx, radius)
            24 -> triangleUp(ctx, radius)
            25 -> triangleDown(ctx, radius)
            else -> throw IllegalStateException("Unknown point shape")
        }
    }

    internal fun circle(ctx: Context2d, r: Double) {
        ctx.arc(0.0, 0.0, r, 0.0, 2 * PI)
    }

    internal fun square(ctx: Context2d, r: Double) {
        ctx.moveTo(-r, -r)
        ctx.lineTo(r, -r)
        ctx.lineTo(r, r)
        ctx.lineTo(-r, r)
        ctx.closePath()
    }

    internal fun squareTriangle(ctx: Context2d, r: Double, stroke: Double) {
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

    internal fun triangleUp(ctx: Context2d, r: Double) {
        val a = 3 * r / sqrt(3.0)

        ctx.moveTo(0.0, -r)
        ctx.lineTo(a / 2, r / 2)
        ctx.lineTo(-a / 2, r / 2)
        ctx.closePath()
    }

    internal fun triangleDown(ctx: Context2d, r: Double) {
        val a = 3 * r / sqrt(3.0)

        ctx.moveTo(0.0, r)
        ctx.lineTo(-a / 2, -r / 2)
        ctx.lineTo(a / 2, -r / 2)
        ctx.closePath()
    }

    internal fun plus(ctx: Context2d, r: Double) {
        ctx.moveTo(0.0, -r)
        ctx.lineTo(0.0, r)
        ctx.moveTo(-r, 0.0)
        ctx.lineTo(r, 0.0)
    }

    internal fun cross(ctx: Context2d, r: Double) {
        ctx.moveTo(-r, -r)
        ctx.lineTo(r, r)
        ctx.moveTo(-r, r)
        ctx.lineTo(r, -r)
    }

    internal fun diamond(ctx: Context2d, r: Double) {
        ctx.moveTo(0.0, -r)
        ctx.lineTo(r, 0.0)
        ctx.lineTo(0.0, r)
        ctx.lineTo(-r, 0.0)
        ctx.closePath()
    }

    fun changeAlphaWithMin(color: Color, newAlpha: Int?): Color {
        return newAlpha?.let { min(it, color.alpha) }?.let(color::changeAlpha) ?: color
    }

}
