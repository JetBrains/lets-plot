package jetbrains.datalore.plot.base.render.point

import jetbrains.datalore.plot.base.DataPointAesthetics

object TinyPointShape : PointShape {

    override val code: Int
        get() = 46 // ASCII dot `.`

    override fun size(dataPoint: DataPointAesthetics): Double {
        return 1.0
    }

    override fun strokeWidth(dataPoint: DataPointAesthetics): Double {
        return 0.0
    }
}
