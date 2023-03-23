/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.aes

import jetbrains.datalore.plot.base.DataPointAesthetics

object AesScaling {
    const val UNIT_SHAPE_SIZE = 2.2

    fun strokeWidth(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * 2.0
    }

    fun circleDiameter(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * UNIT_SHAPE_SIZE
    }

    fun pointStrokeWidth(p: DataPointAesthetics): Double {
        return p.stroke()!! * UNIT_SHAPE_SIZE
    }

    fun pieDiameter(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * 10.0
    }

    fun circleDiameterSmaller(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * 1.5
    }

    fun sizeFromCircleDiameter(diameter: Double): Double {
        // px -> aes Units
        return diameter / UNIT_SHAPE_SIZE
    }

    fun textSize(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * 2
    }

}