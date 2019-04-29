package jetbrains.datalore.visualization.plot.gog.core.render.geom.util

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics

class MultiPointData internal constructor(
        val aes: DataPointAesthetics,
        val points: List<DoubleVector>,
        val localToGlobalIndex: (Int) -> Int,
        val group: Int)
