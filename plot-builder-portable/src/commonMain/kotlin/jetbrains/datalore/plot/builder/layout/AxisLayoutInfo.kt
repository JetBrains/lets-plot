/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.guide.Orientation

class AxisLayoutInfo constructor(
    val axisLength: Double,
    val axisDomain: DoubleSpan,
    val orientation: Orientation,
    val axisBreaks: ScaleBreaks,

    val tickLabelsBounds: DoubleRectangle,
    val tickLabelRotationAngle: Double,
    val tickLabelHorizontalAnchor: Text.HorizontalAnchor? = null,
    val tickLabelVerticalAnchor: Text.VerticalAnchor? = null,
    val tickLabelAdditionalOffsets: List<DoubleVector>? = null,
    internal val tickLabelsTextBounds: DoubleRectangle? = null                    // without margins - debug
) {

    fun withAxisLength(axisLength: Double): AxisLayoutInfo {
        return AxisLayoutInfo(
            axisLength = axisLength,
            axisDomain = axisDomain,
            orientation = orientation,
            axisBreaks = axisBreaks,
            tickLabelsBounds = tickLabelsBounds,
            tickLabelRotationAngle = tickLabelRotationAngle,
            tickLabelHorizontalAnchor = tickLabelHorizontalAnchor,
            tickLabelVerticalAnchor = tickLabelVerticalAnchor,
            tickLabelAdditionalOffsets = tickLabelAdditionalOffsets,
            tickLabelsTextBounds = tickLabelsTextBounds
        )
    }

    fun axisBounds(): DoubleRectangle {
        return tickLabelsBounds.union(DoubleRectangle(0.0, 0.0, 0.0, 0.0))
    }

    fun axisBoundsAbsolute(geomBounds: DoubleRectangle): DoubleRectangle {
        val axisBounds = axisBounds()

        val orig = if (orientation.isHorizontal) {
            val top = when (orientation) {
                Orientation.TOP -> geomBounds.top - axisBounds.height
                else -> geomBounds.bottom
            }
            DoubleVector(geomBounds.left + axisBounds.left, top)
        } else {
            val left = when (orientation) {
                Orientation.LEFT -> geomBounds.left - axisBounds.width
                else -> geomBounds.right
            }
            DoubleVector(left, geomBounds.top + axisBounds.top)
        }
        return DoubleRectangle(orig, axisBounds.dimension)
    }
}
