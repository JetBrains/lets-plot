package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.DataPointAesthetics

class MultiPointData internal constructor(
    val aes: DataPointAesthetics,
    val points: List<DoubleVector>,
    val localToGlobalIndex: (Int) -> Int,
    val group: Int)
