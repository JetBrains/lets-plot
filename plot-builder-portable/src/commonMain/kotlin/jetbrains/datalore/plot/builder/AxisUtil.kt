/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.guide.AxisComponent

object AxisUtil {
    fun breaksData(
        scaleBreaks: ScaleBreaks,
        scaleMapper: ScaleMapper<Double>,
        coord: CoordinateSystem,
        horizontal: Boolean
    ): AxisComponent.BreaksData {
        val mappedBreaks = toAxisCoord(scaleBreaks, scaleMapper, coord, horizontal)
        return AxisComponent.BreaksData(
            majorBreaks = mappedBreaks,
            majorLabels = scaleBreaks.labels
        )
    }

    private fun toAxisCoord(
        scaleBreaks: ScaleBreaks,
        scaleMapper: ScaleMapper<Double>,
        coord: CoordinateSystem,
        horizontal: Boolean
    ): List<Double> {
        val breaksMapped = scaleBreaks.transformedValues.map {
            // Don't expect NULLs.
            scaleMapper(it) as Double
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
                val orient = if (horizontal) "horizontal" else "vertical"
                throw IllegalStateException(
                    "Illegal axis '" + orient + "' break position " + axisBr +
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
