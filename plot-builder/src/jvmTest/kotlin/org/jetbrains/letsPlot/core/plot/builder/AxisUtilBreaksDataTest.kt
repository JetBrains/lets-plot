/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeUtil
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import kotlin.test.Test
import kotlin.test.assertEquals

class AxisUtilBreaksDataTest {
    private val axisTheme: AxisTheme = ThemeUtil.buildTheme(ThemeOption.Name.LP_MINIMAL)
        .horizontalAxis(flipAxis = false)

    @Test
    fun majorLabelOffsetsStayAlignedAfterOverlapFiltering() {
        val offsets = distinctOffsets(count = 4)
        val breaksData = AxisUtil.breaksData(
            breakTransformedValues = listOf(1.0, 2.0, 3.0, 4.0),
            breakLabels = listOf("one", "two", "three", "four"),
            coord = IdentityCoordinateSystem,
            dataDomain = DATA_DOMAIN,
            flipAxis = false,
            orientation = Orientation.BOTTOM,
            axisTheme = axisTheme,
            labelAdjustments = AxisComponent.TickLabelAdjustments(
                orientation = Orientation.BOTTOM,
                bounds = listOf(
                    DoubleRectangle.XYWH(0.0, 0.0, 20.0, 10.0),
                    DoubleRectangle.XYWH(10.0, 0.0, 20.0, 10.0),
                    DoubleRectangle.XYWH(40.0, 0.0, 20.0, 10.0),
                    DoubleRectangle.XYWH(50.0, 0.0, 20.0, 10.0),
                ),
                additionalOffsets = offsets
            )
        )

        assertEquals(listOf("one", "three"), breaksData.majorLabels)
        assertEquals(offsetsWithBase(offsets, 0, 2), breaksData.majorLabelOffsets)
    }

    @Test
    fun majorLabelOffsetsStayAlignedAfterGridLineFiltering() {
        val offsets = distinctOffsets(count = 4)
        val breaksData = AxisUtil.breaksData(
            breakTransformedValues = listOf(1.0, 2.0, 3.0, 4.0),
            breakLabels = listOf("one", "two", "three", "four"),
            coord = NullGridLineEndpointCoordinateSystem(nullGridLineTick = 3.0),
            dataDomain = DATA_DOMAIN,
            flipAxis = false,
            orientation = Orientation.BOTTOM,
            axisTheme = axisTheme,
            labelAdjustments = AxisComponent.TickLabelAdjustments(
                orientation = Orientation.BOTTOM,
                bounds = listOf(
                    DoubleRectangle.XYWH(0.0, 0.0, 20.0, 10.0),
                    DoubleRectangle.XYWH(40.0, 0.0, 20.0, 10.0),
                    DoubleRectangle.XYWH(80.0, 0.0, 20.0, 10.0),
                    DoubleRectangle.XYWH(120.0, 0.0, 20.0, 10.0),
                ),
                additionalOffsets = offsets
            )
        )

        assertEquals(listOf("one", "two", "four"), breaksData.majorLabels)
        assertEquals(offsetsWithBase(offsets, 0, 1, 3), breaksData.majorLabelOffsets)
    }

    private fun distinctOffsets(count: Int): List<DoubleVector> {
        return (0 until count).map { DoubleVector(it.toDouble(), it.toDouble() * 10.0) }
    }

    private fun offsetsWithBase(offsets: List<DoubleVector>, vararg indices: Int): List<DoubleVector> {
        val baseOffset = AxisUtil.tickLabelBaseOffset(axisTheme, Orientation.BOTTOM)
        return indices.map { baseOffset.add(offsets[it]) }
    }

    private object IdentityCoordinateSystem : CoordinateSystem {
        override val isLinear: Boolean = true
        override val isPolar: Boolean = false

        override fun toClient(p: DoubleVector): DoubleVector = p

        override fun fromClient(p: DoubleVector): DoubleVector = p

        override fun unitSize(p: DoubleVector): DoubleVector = DoubleVector(1.0, 1.0)

        override fun flip(): CoordinateSystem = this
    }

    private class NullGridLineEndpointCoordinateSystem(
        private val nullGridLineTick: Double
    ) : CoordinateSystem {
        override val isLinear: Boolean = true
        override val isPolar: Boolean = false

        override fun toClient(p: DoubleVector): DoubleVector? {
            return if (p == DoubleVector(nullGridLineTick, DATA_DOMAIN.yRange().lowerEnd)) {
                null
            } else {
                p
            }
        }

        override fun fromClient(p: DoubleVector): DoubleVector = p

        override fun unitSize(p: DoubleVector): DoubleVector = DoubleVector(1.0, 1.0)

        override fun flip(): CoordinateSystem = this
    }

    companion object {
        private val DATA_DOMAIN = DoubleRectangle.XYWH(0.0, 0.0, 10.0, 10.0)
    }
}
