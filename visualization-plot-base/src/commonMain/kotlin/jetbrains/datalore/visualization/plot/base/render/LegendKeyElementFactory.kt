package jetbrains.datalore.visualization.plot.base.render

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.plot.base.DataPointAesthetics
import jetbrains.datalore.visualization.plot.base.aes.AesScaling

interface LegendKeyElementFactory {
    fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement

    fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val strokeWidth = AesScaling.strokeWidth(p)
        val size = 2 * strokeWidth + 4
        return DoubleVector(size, size)
    }

}
