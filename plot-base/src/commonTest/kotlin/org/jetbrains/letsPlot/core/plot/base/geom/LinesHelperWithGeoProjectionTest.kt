/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.projections.azimuthalEqualArea
import org.jetbrains.letsPlot.core.plot.base.BogusContext
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.coord.CoordinatesMapper
import org.jetbrains.letsPlot.core.plot.base.coord.Coords
import org.jetbrains.letsPlot.core.plot.base.geom.util.LinesHelper
import org.jetbrains.letsPlot.core.plot.base.pos.PositionAdjustments
import kotlin.test.Test

class LinesHelperWithGeoProjectionTest {

    private val coordinateSystem = Coords.create(
        CoordinatesMapper.create(
            adjustedDomain = DoubleRectangle.LTRB(0, 0, 200, 200),
            clientSize = DoubleVector(200.0, 200.0),
            projection = azimuthalEqualArea(),
            flipAxis = false
        )
    )
    private var linesHelper: LinesHelper = LinesHelper(
        PositionAdjustments.identity(), coordinateSystem,
        BogusContext
    ).apply {
        setResamplingEnabled(true)
    }

    @Test
    fun should_not_fail_when_path_end_point_projected_to_null() {
        // 180, 0 can't be projected in azimuthalEqualArea
        val aes = AestheticsBuilder(4)
            .x(listOf(-180.0, 0.0, 0.0, 180.0)::get)
            .y(listOf(0.0, 0.0, 0.0, 0.0)::get)
            .build()

        linesHelper.createPathData(aes.dataPoints())
    }

    @Test
    fun should_not_fail_when_all_path_points_projected_to_null_giving_empty_path() {
        val aes = AestheticsBuilder(1)
            .x(listOf(180.0)::get)
            .y(listOf(0.0)::get)
            .build()

        linesHelper.createPathData(aes.dataPoints())
    }
}