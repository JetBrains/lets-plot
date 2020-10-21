/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.aes

import jetbrains.datalore.plot.base.DataPointAesthetics

object AesScaling {
    private const val CIRCLE_DIAMETER_FACTOR = 2.2

    fun strokeWidth(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * 2.0
    }

    fun circleDiameter(p: DataPointAesthetics): Double {
        // aes Units -> px
        return circleDiameter(p.size()!!)
    }

    fun circleDiameter(size: Double): Double {
        // aes Units -> px
        return size * CIRCLE_DIAMETER_FACTOR
    }

    fun circleDiameterSmaller(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * 1.5
    }

    fun sizeFromCircleDiameter(diameter: Double): Double {
        // px -> aes Units
        return diameter / CIRCLE_DIAMETER_FACTOR
    }

    fun textSize(p: DataPointAesthetics): Double {
        // aes Units -> px
        return textSize(p.size()!!)
    }

    fun textSize(size: Double) = size * 2

}