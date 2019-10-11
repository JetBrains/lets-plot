package jetbrains.datalore.plot.base

import jetbrains.datalore.base.geometry.DoubleVector

interface PositionAdjustment {
    val isIdentity: Boolean
        get() = false

    fun handlesGroups(): Boolean

    fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector
}
