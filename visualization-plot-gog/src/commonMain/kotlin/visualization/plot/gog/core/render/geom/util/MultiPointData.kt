package jetbrains.datalore.visualization.plot.gog.core.render.geom.util

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics

class MultiPointData internal constructor(
        val aes: DataPointAesthetics,
        val points: List<DoubleVector>,
        val localToGlobalIndex: Function<Int, Int>,
        val group: Int)
