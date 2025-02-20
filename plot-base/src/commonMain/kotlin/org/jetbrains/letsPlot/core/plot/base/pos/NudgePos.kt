/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.pos

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.DimensionUnit

internal class NudgePos(
    width: Double?,
    height: Double?,
    private val unit: DimensionUnit
) : PositionAdjustment {
    private val width: Double = width ?: DEF_NUDGE_WIDTH
    private val height: Double = height ?: DEF_NUDGE_HEIGHT

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        val coord = ctx.getCoordinateSystem() ?: return v

        return when (unit) {
            DimensionUnit.RESOLUTION -> error("Unsupported unit: $unit")
            DimensionUnit.IDENTITY -> v.add(DoubleVector(width, height))
            DimensionUnit.SIZE -> {
                val originClient = coord.toClient(v) ?: error("Failed to convert origin to client coordinates")
                val unitSize = DoubleVector(
                    coord.unitSize(DoubleVector(1.0, 0.0)).x,
                    coord.unitSize(DoubleVector(0.0, 1.0)).y
                )
                val transformedOrigin = originClient.add(DoubleVector(
                    width * AesScaling.POINT_UNIT_SIZE / unitSize.x,
                    height * AesScaling.POINT_UNIT_SIZE / unitSize.y))

                coord.fromClient(transformedOrigin) ?: error("Failed to convert transformed origin from client coordinates")
            }
            DimensionUnit.PIXEL -> {
                val originClient = coord.toClient(v) ?: error("Failed to convert origin to client coordinates")
                val transformedOrigin = originClient.add(DoubleVector(width, height))
                coord.fromClient(transformedOrigin) ?: error("Failed to convert transformed origin from client coordinates")
            }
        }
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.NUDGE.handlesGroups()
    }

    companion object {
        const val DEF_NUDGE_WIDTH = 0.0
        const val DEF_NUDGE_HEIGHT = 0.0
    }
}
