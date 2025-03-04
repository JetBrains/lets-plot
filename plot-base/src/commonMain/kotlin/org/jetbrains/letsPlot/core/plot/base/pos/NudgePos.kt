/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.pos

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.DimensionUnit

class NudgePos(
    width: Double?,
    height: Double?,
    val unit: DimensionUnit
) : PositionAdjustment {
    private val width: Double = width ?: DEF_NUDGE_WIDTH
    private val height: Double = height ?: DEF_NUDGE_HEIGHT

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        val coord = ctx.getCoordinateSystem() ?: return v

        return when (unit) {
            DimensionUnit.RESOLUTION -> error("Unsupported unit: $unit")
            DimensionUnit.IDENTITY -> v.add(adjustedDimension)
            DimensionUnit.SIZE -> {
                val originClient = coord.toClient(v) ?: error("Failed to convert origin to client coordinates")

                val transformedOrigin = originClient.add(adjustedDimension)

                coord.fromClient(transformedOrigin) ?: error("Failed to convert transformed origin from client coordinates")
            }
            DimensionUnit.PIXEL -> {
                val originClient = coord.toClient(v) ?: error("Failed to convert origin to client coordinates")
                val transformedOrigin = originClient.add(adjustedDimension)
                coord.fromClient(transformedOrigin) ?: error("Failed to convert transformed origin from client coordinates")
            }
        }
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.NUDGE.handlesGroups()
    }

    val adjustedDimension: DoubleVector
        get() = when (unit) {
            DimensionUnit.RESOLUTION -> error("Unsupported unit: $unit")
            DimensionUnit.IDENTITY -> DoubleVector(width, height)
            DimensionUnit.SIZE -> DoubleVector(width * AesScaling.POINT_UNIT_SIZE, -height * AesScaling.POINT_UNIT_SIZE)
            DimensionUnit.PIXEL -> DoubleVector(width, -height)
        }

    companion object {
        const val DEF_NUDGE_WIDTH = 0.0
        const val DEF_NUDGE_HEIGHT = 0.0
    }
}
