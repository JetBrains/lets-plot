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
import jetbrains.datalore.plot.builder.guide.AxisComponent

object AxisUtil {
    fun breaksData(
        scale: Scale<Double>,
        coord: CoordinateSystem,
        horizontal: Boolean
    ): AxisComponent.BreaksData {
        val scaleBreaks = scale.getScaleBreaks()
        val mappedBreaks = toAxisCoord(scaleBreaks, scale, coord, horizontal)
        return AxisComponent.BreaksData(
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
