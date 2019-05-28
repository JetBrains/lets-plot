package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

interface SvgLocatable {

    val bBox: DoubleRectangle
    fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector

    fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector
}