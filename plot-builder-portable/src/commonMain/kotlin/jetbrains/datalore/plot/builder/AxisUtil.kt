/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.guide.AxisComponent

object AxisUtil {
    fun breaksData(
        scaleBreaks: ScaleBreaks,
        coord: CoordinateSystem,
        flipAxis: Boolean,
        horizontal: Boolean
    ): AxisComponent.BreaksData {
        val (breakCoords, breakLabels) = toAxisCoord(
            scaleBreaks,
            coord,
            flipAxis,
            horizontal
        )
        return AxisComponent.BreaksData(
            majorBreaks = breakCoords,
            majorLabels = breakLabels
        )
    }

    private fun toAxisCoord(
        scaleBreaks: ScaleBreaks,
        coord: CoordinateSystem,
        flipAxis: Boolean,
        horizontal: Boolean
    ): Pair<List<Double>, List<String>> {
        val breaksDataAndLabel: List<Pair<Double, String>> = scaleBreaks.transformedValues.zip(scaleBreaks.labels)

        val axisBreaks = ArrayList<Double>()
        val axisLabels = ArrayList<String>()
        for ((br, label) in breaksDataAndLabel) {
            // ToDo: the second coordinate should be taken from "valid domain"
            val bpCoord = when (horizontal) {
                true -> DoubleVector(br, 0.0)
                false -> DoubleVector(0.0, br)
            }.let {
                if (flipAxis) {
                    it.flip()
                } else {
                    it
                }
            }

            val bpClientCoord = coord.toClient(bpCoord)
            if (!(bpClientCoord != null && bpClientCoord.isFinite)) {
                // skip this break-point: it's outside the coordinate system' domain.
                continue
            }

            val bpOnAxis = if (horizontal)
                bpClientCoord.x
            else
                bpClientCoord.y

            axisBreaks.add(bpOnAxis)
            axisLabels.add(label)
        }
        return Pair(axisBreaks, axisLabels)
    }
}
