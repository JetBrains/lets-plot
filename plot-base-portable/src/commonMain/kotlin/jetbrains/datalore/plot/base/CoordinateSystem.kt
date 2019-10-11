package jetbrains.datalore.plot.base

import jetbrains.datalore.base.geometry.DoubleVector

interface CoordinateSystem {
    fun toClient(p: DoubleVector): DoubleVector

    fun fromClient(p: DoubleVector): DoubleVector
}
