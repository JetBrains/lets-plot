package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleVector

interface RenderBox : RenderObject {
    val origin: DoubleVector
    val dimension: DoubleVector
}