/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.pos

import demoAndTestShared.assertEquals
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.BogusContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil

open class PosTest {
    private val doubleComparisonPrecision = 1e-9

    protected fun compareWithExpectedOffsets(
        xValues: List<Double?>,
        yValues: List<Double?>,
        groupValues: List<Int>? = null,
        expectedYOffsets: List<Double?>,
        posConstructor: (Aesthetics) -> PositionAdjustment,
        messageBeginning: String
    ) {
        val aes = buildAesthetics(xValues, yValues, groupValues)
        val pos = posConstructor(aes)
        for (i in 0 until aes.dataPointCount()) {
            val p = aes.dataPointAt(i)
            if (!SeriesUtil.allFinite(p.x(), p.y())) continue
            val translatedPoint = pos.translate(DoubleVector(p.x()!!, p.y()!!), p,
                BogusContext
            )
            assertEquals(
                expectedYOffsets[i],
                translatedPoint.y,
                doubleComparisonPrecision,
                "$messageBeginning at $i-th point $translatedPoint."
            )
        }
    }

    private fun buildAesthetics(
        xValues: List<Double?>,
        yValues: List<Double?>,
        groupValues: List<Int>?
    ): Aesthetics {
        val builder = AestheticsBuilder(xValues.size)
            .x(AestheticsBuilder.list(xValues))
            .y(AestheticsBuilder.list(yValues))
        if (groupValues != null) builder.group(AestheticsBuilder.list(groupValues))
        return builder.build()
    }
}