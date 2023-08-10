/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.aes

import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics

object AesScaling {
    const val POINT_UNIT_SIZE = 2.2
    const val PIE_UNIT_SIZE = 10.0

    fun strokeWidth(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * POINT_UNIT_SIZE
    }

    fun pointStrokeWidth(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.stroke()!! * POINT_UNIT_SIZE
    }

    fun lineWidth(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.linewidth()!! * POINT_UNIT_SIZE
    }

    fun circleDiameter(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * POINT_UNIT_SIZE
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

}