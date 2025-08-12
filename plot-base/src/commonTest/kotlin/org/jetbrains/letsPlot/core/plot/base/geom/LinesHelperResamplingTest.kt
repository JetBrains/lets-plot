/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.list
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.TO_LOCATION_X_Y
import org.jetbrains.letsPlot.core.plot.base.geom.util.LinesHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.QuantilesHelper
import org.jetbrains.letsPlot.core.plot.base.pos.PositionAdjustments
import org.jetbrains.letsPlot.core.plot.base.stat.DensityStat
import org.jetbrains.letsPlot.core.plot.builder.PosProviderContext
import org.jetbrains.letsPlot.core.plot.builder.assemble.PosProvider
import org.jetbrains.letsPlot.core.plot.builder.coord.PolarCoordProvider
import org.jetbrains.letsPlot.core.plot.builder.scale.mapper.ColorMapper
import kotlin.test.Test
import kotlin.test.assertContentEquals

class LinesHelperResamplingTest {
    private fun colorMapper(values: Iterable<Double>): (Double?) -> Color {
        return ColorMapper.gradient(DoubleSpan.encloseAll(values), Color.DARK_BLUE, Color.LIGHT_BLUE, Color.GRAY)
    }

    private fun toColors(values: Iterable<Double>): List<Color> {
        val mapper = colorMapper(values)
        val colors = ArrayList<Color>()
        for (value in values) {
            colors.add(mapper(value))
        }
        return colors
    }

    private fun createPositionAdjustment(posProvider: PosProvider, aes: Aesthetics): PositionAdjustment {
        return posProvider.createPos(object : PosProviderContext {
            override val aesthetics: Aesthetics
                get() = aes

            override val groupCount: Int
                    by lazy {
                        val set = aes.groups().toSet()
                        set.size
                    }
        })
    }

    @Test
    fun resampling_in_polar_coords_by_group() {
        val x = listOf(1.0, 1.0, 1.5, 1.5, 2.0, 1.0, 1.0, 1.5, 1.5, 2.0)
        val y = listOf(0.3989422804014327,
            0.3989422804014327,
            0.3520653267642995,
            0.3520653267642995,
            0.24197072451914337,
            0.5287675721190984,
            0.5287675721190984,
            0.450988942775473,
            0.450988942775473,
            0.5287675721190984)
        val fill = list(toColors(listOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0)))
        val group = listOf(0, 0, 0, 0, 0, 1, 1, 1, 1, 1)
        val aes = AestheticsBuilder(10)
            .x(x::get)
            .y(y::get)
            .fill(fill)
            .group(group::get)
            .build()

        val domain = DoubleRectangle.LTRB(0, 0, 360, 4)
        val clientSize = DoubleVector(200.0, 200.0)

        val polarCoordProvider = PolarCoordProvider(
            xLim = Pair(null, null),
            yLim = Pair(null, null),
            xReversed = false,
            yReversed = false,
            flipped = true,
            0.0,
            true,
            transformBkgr = false
        )
        val adjustedDomain = polarCoordProvider.adjustDomain(domain)

        val coordinateSystem = polarCoordProvider.createCoordinateSystem(adjustedDomain, clientSize)

        val posProvider = PosProvider.barStack()

        val posAdj = createPositionAdjustment(posProvider, aes)

        val linesHelper: LinesHelper = LinesHelper(
            posAdj, coordinateSystem,
            BogusContext
        ).apply {
            setResamplingEnabled(true)
            setResamplingPrecision(1.0)
        }

        val quantilesHelper = QuantilesHelper(PositionAdjustments.identity(), coordinateSystem,
            BogusContext, DensityStat.DEF_QUANTILES)

        val dataPoints = GeomUtil.withDefined(GeomUtil.ordered_X(aes.dataPoints()), Aes.X, Aes.Y)

        val actual = HashMap<Int, List<DoubleVector>>()

        dataPoints.sortedByDescending(DataPointAesthetics::group).groupBy(DataPointAesthetics::group)
            .forEach { (_, groupDataPoints) ->
                quantilesHelper.splitByQuantiles(groupDataPoints, Aes.X).forEach { points ->
                    val m = linesHelper.createPathData(points, TO_LOCATION_X_Y, true)
                    m.map { (group, pathData) ->
                        actual.put(group, pathData.flatMap { it.coordinates })
                    }
                }
            }

        val expected0 = listOf(
             DoubleVector(100.14385987378398, 75.00041391669224),
             DoubleVector(100.14385987378398, 75.00041391669224),
             DoubleVector(100.16925996079176, 68.75045838631112),
             DoubleVector(100.19043407200877, 62.50048353826122),
             DoubleVector(100.19043407200877, 62.50048353826122),
             DoubleVector(100.1874353635747, 56.25040151059119),
             DoubleVector(100.17451145710753, 50.000304543414074),
             DoubleVector(100.17333681283097, 62.50040061081563),
             DoubleVector(100.14385987378398, 75.00041391669224)
        )

        val expected1 = listOf(
            DoubleVector(100.3345270291754, 75.00223826686175),
            DoubleVector(100.3345270291754, 75.00223826686175),
            DoubleVector(100.40063067882464, 68.7525681845822),
            DoubleVector(100.43436862535268, 62.502515765756925),
            DoubleVector(100.43436862535268, 62.502515765756925),
            DoubleVector(100.53130312591081, 56.2532262105147),
            DoubleVector(100.55585305177, 50.003089821621586),
            DoubleVector(100.41688978882749, 62.502317366216204),
            DoubleVector(100.3345270291754, 75.00223826686175)
        )

        assertContentEquals(expected0, actual[0])
        assertContentEquals(expected1, actual[1])
    }
}