/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.guide.AxisComponent

object AxisUtil {
    fun breaksData(
        scaleBreaks: ScaleBreaks,
        coord: CoordinateSystem,
        horizontal: Boolean
    ): AxisComponent.BreaksData {
        val (breakCoords, breakLabels) = toAxisCoord(scaleBreaks, coord, horizontal)
        return AxisComponent.BreaksData(
            majorBreaks = breakCoords,
            majorLabels = breakLabels
        )
    }

    private fun toAxisCoord(
        scaleBreaks: ScaleBreaks,
        coord: CoordinateSystem,
        horizontal: Boolean
    ): Pair<List<Double>, List<String>> {
        val breaksDataAndLabel: List<Pair<Double, String>> = scaleBreaks.transformedValues.zip(scaleBreaks.labels)

        val axisBreaks = ArrayList<Double>()
        val axisLabels = ArrayList<String>()
        for ((br, label) in breaksDataAndLabel) {
            // ToDo: the second coordinate should be taken from "valid domain"
            val mappedBrPoint = when (horizontal) {
                true -> DoubleVector(br, 0.0)
                false -> DoubleVector(0.0, br)
            }

            val axisBrPoint = coord.toClient(mappedBrPoint)
            if (!(axisBrPoint != null && axisBrPoint.isFinite)) {
                // skip this break-point: it's outside the coordinate system' domain.
                continue
            }

            val brCoord = if (horizontal)
                axisBrPoint.x
            else
                axisBrPoint.y

            axisBreaks.add(brCoord)
            axisLabels.add(label)
//            if (!axisBr.isFinite()) {
//                val orient = if (horizontal) "horizontal" else "vertical"
//                throw IllegalStateException(
//                    "Illegal axis '" + orient + "' break position " + axisBr +
//                            " at index " + (axisBreaks.size - 1) +
//                            "\nsource breaks    : " + scaleBreaks.domainValues +
//                            "\ntranslated breaks: " + breaksMapped +
//                            "\naxis breaks      : " + axisBreaks
//                )
//            }
        }
        return Pair(axisBreaks, axisLabels)
    }
}
