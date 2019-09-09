package jetbrains.datalore.visualization.plot.base.aes

import jetbrains.datalore.visualization.plot.base.DataPointAesthetics

object AesScaling {
    fun strokeWidth(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * 2.0
    }

    fun circleDiameter(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * 2.2
    }

    fun circleDiameterSmaller(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * 1.5
    }

    fun sizeFromCircleDiameter(diameter: Double): Double {
        // px -> aes Units
        return diameter / 2.2
    }

    fun textSize(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * 2
    }

}