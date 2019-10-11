package jetbrains.datalore.vis.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

interface SvgPlatformPeer {
    fun getComputedTextLength(node: SvgTextContent): Double

    fun invertTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector

    fun applyTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector

    fun getBBox(element: SvgLocatable): DoubleRectangle
}