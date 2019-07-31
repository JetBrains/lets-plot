package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleVector

interface RenderBox : RenderObject {
    fun origin(): DoubleVector
    fun dimension(): DoubleVector
}