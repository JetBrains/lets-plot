/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.builder.guide.AxisComponent2

object AxisUtil {
//    fun applyLayoutInfo(axis: AxisComponent, info: AxisLayoutInfo) {
//        axis.tickLabelOffsets.set(info.tickLabelAdditionalOffsets)
//        axis.tickLabelRotationDegree.set(info.tickLabelRotationAngle)
//        if (info.tickLabelHorizontalAnchor != null) {
//            axis.tickLabelHorizontalAnchor.set(info.tickLabelHorizontalAnchor)
//        }
//        if (info.tickLabelVerticalAnchor != null) {
//            axis.tickLabelVerticalAnchor.set(info.tickLabelVerticalAnchor)
//        }
//        axis.tickLabelSmallFont.set(info.tickLabelSmallFont)
//    }

//    fun applyTheme(axis: AxisComponent, theme: AxisTheme, hideAxisBreaks: Boolean = false) {
//        if (!hideAxisBreaks) {
//            axis.tickLabelsEnabled().set(theme.showLabels())
//            axis.tickMarksEnabled().set(theme.showTickMarks())
//        } else {
//            axis.tickLabelsEnabled().set(false)
//            axis.tickMarksEnabled().set(false)
//        }
//        axis.axisLineEnabled().set(theme.showLine())
//
//        axis.lineWidth.set(theme.lineWidth())
//        axis.tickMarkLength.set(theme.tickMarkLength())
//        axis.tickMarkPadding.set(theme.tickMarkPadding())
//        axis.tickMarkWidth.set(theme.tickMarkWidth())
//    }

//    fun setBreaks(axis: AxisComponent, scale: Scale<Double>, coord: CoordinateSystem, horizontal: Boolean) {
//        val scaleBreaks = scale.getScaleBreaks()
//        val mappedBreaks = toAxisCoord(scaleBreaks, scale, coord, horizontal)
//
//        axis.breaks.set(mappedBreaks)
//        axis.labels.set(scaleBreaks.labels)
//    }

    fun breaksData(
        scale: Scale<Double>,
        coord: CoordinateSystem,
        horizontal: Boolean
    ): AxisComponent2.BreaksData {
        val scaleBreaks = scale.getScaleBreaks()
        val mappedBreaks = toAxisCoord(scaleBreaks, scale, coord, horizontal)
        return AxisComponent2.BreaksData(
            majorBreaks = mappedBreaks,
            majorLabels = scaleBreaks.labels
        )
    }

    private fun toAxisCoord(
        scaleBreaks: ScaleBreaks,
        scale: Scale<Double>,
        coord: CoordinateSystem,
        horizontal: Boolean
    ): List<Double> {
        val breaksMapped = ScaleUtil.map(scaleBreaks.transformedValues, scale).map {
            // Don't expect NULLs.
            it as Double
        }
        val axisBreaks = ArrayList<Double>()
        for (br in breaksMapped) {
            val mappedBrPoint = when (horizontal) {
                true -> DoubleVector(br, 0.0)
                false -> DoubleVector(0.0, br)
            }

            val axisBrPoint = coord.toClient(mappedBrPoint)
            val axisBr = if (horizontal)
                axisBrPoint.x
            else
                axisBrPoint.y

            axisBreaks.add(axisBr)
            if (!axisBr.isFinite()) {
                throw IllegalStateException(
                    "Illegal axis '" + scale.name + "' break position " + axisBr +
                            " at index " + (axisBreaks.size - 1) +
                            "\nsource breaks    : " + scaleBreaks.domainValues +
                            "\ntranslated breaks: " + breaksMapped +
                            "\naxis breaks      : " + axisBreaks
                )
            }
        }
        return axisBreaks
    }
}
