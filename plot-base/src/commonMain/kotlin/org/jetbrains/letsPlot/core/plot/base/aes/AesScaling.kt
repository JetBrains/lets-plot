/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.aes

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics

object AesScaling {
    const val POINT_UNIT_SIZE = 2.2
    const val PIE_UNIT_SIZE = 10.0

    fun strokeWidth(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * POINT_UNIT_SIZE
    }

    fun pointStrokeWidth(
        p: DataPointAesthetics,
        strokeGetter: (DataPointAesthetics) -> Double? = DataPointAesthetics::stroke
    ): Double {
        // aes Units -> px
        return strokeGetter(p)!! * POINT_UNIT_SIZE
    }

    fun lineWidth(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.linewidth()!! * POINT_UNIT_SIZE
    }

    fun circleDiameter(
        p: DataPointAesthetics,
        sizeGetter: (DataPointAesthetics) -> Double? = DataPointAesthetics::size
    ): Double {
        // aes Units -> px
        return sizeGetter(p)!! * POINT_UNIT_SIZE
    }

    fun pieDiameter(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * PIE_UNIT_SIZE
    }

    fun circleDiameterSmaller(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * 1.5
    }

    fun sizeFromCircleDiameter(diameter: Double): Double {
        // px -> aes Units
        return diameter / POINT_UNIT_SIZE
    }

    fun textSize(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * 2
    }

    private fun targetSize(p: DataPointAesthetics, atStart: Boolean): Double {
        // px -> aes Units
        val sizeAes = if (atStart) DataPointAesthetics::sizeStart else DataPointAesthetics::sizeEnd
        val strokeAes = if (atStart) DataPointAesthetics::strokeStart else DataPointAesthetics::strokeEnd
        return circleDiameter(p, sizeAes) / 2 + pointStrokeWidth(p, strokeAes)
    }

    fun targetStartSize(p: DataPointAesthetics): Double {
        // px -> aes Units
        return targetSize(p, true)
    }

    fun targetEndSize(p: DataPointAesthetics): Double {
        // px -> aes Units
        return targetSize(p, false)
    }

    fun sizeUnitRatio(
        p: DoubleVector,
        coord: CoordinateSystem,
        axis: String?,
        baseUnitSize: Double
    ): Double {
        if (axis.isNullOrBlank()) return 1.0
        val unitSquareSize = coord.unitSize(p)

        val unitSize = when (axis.lowercase()) {
            "x" -> unitSquareSize.x
            "y" -> unitSquareSize.y
            "min" -> maxOf(unitSquareSize.x, unitSquareSize.y)
            "max" -> minOf(unitSquareSize.x, unitSquareSize.y)
            else -> error("Size unit value must be either 'x' or 'y', but was '$axis'.")
        }

        return unitSize / baseUnitSize
    }
}