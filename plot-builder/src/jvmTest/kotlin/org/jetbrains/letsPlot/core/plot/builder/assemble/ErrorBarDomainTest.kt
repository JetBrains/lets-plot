/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import demoAndTestShared.assertEquals
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.geom.ErrorBarGeom
import org.jetbrains.letsPlot.core.plot.base.pos.PositionAdjustments
import org.jetbrains.letsPlot.core.plot.base.scale.Scales
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.assemble.geom.GeomProvider
import org.jetbrains.letsPlot.core.plot.builder.assemble.tiles.SimplePlotGeomTiles
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProviders
import kotlin.test.Test
import kotlin.to

class ErrorBarDomainTest {
    @Test
    fun testErrorBarGeomCalculatesYRangeCorrectly() {
        val errorBarTile = buildErrorBar(isVertical = true)
        val (_, yDomain) = errorBarTile.overallXYContinuousDomains()
        assertEquals(MIN_VALUE - 0.05, yDomain?.lowerEnd, EPSILON)
        assertEquals(MAX_VALUE + 0.05, yDomain?.upperEnd, EPSILON)
    }

    @Test
    fun testErrorBarGeomCalculatesRotatedYRangeCorrectly() {
        val errorBarTile = buildErrorBar(isVertical = false)
        val (xDomain, _) = errorBarTile.overallXYContinuousDomains()
        assertEquals(MIN_VALUE - 0.05, xDomain?.lowerEnd, EPSILON)
        assertEquals(MAX_VALUE + 0.05, xDomain?.upperEnd, EPSILON)
    }

    private fun buildErrorBar(
        isVertical: Boolean
    ): SimplePlotGeomTiles {
        val xAes = if (isVertical) Aes.X else Aes.Y
        val yAes = if (isVertical) Aes.Y else Aes.X
        val yMinAes = if (isVertical) Aes.YMIN else Aes.XMIN
        val yMaxAes = if (isVertical) Aes.YMAX else Aes.XMAX
        val catValues = listOf("a")
        val catVar = DataFrame.Variable("cat")
        val minVar = DataFrame.Variable("min")
        val maxVar = DataFrame.Variable("max")

        val geomProvider = GeomProvider.errorBar { ErrorBarGeom(isVertical = isVertical) }
        val data = DataFrame.Builder()
            .put(catVar, catValues)
            .put(minVar, listOf(MIN_VALUE))
            .put(maxVar, listOf(MAX_VALUE))
            .build()
        val continuousScale = Scales.DemoAndTest.continuousDomain(yAes.name, yAes)
        val scaleByAes = mapOf<Aes<*>, Scale>(
            xAes to Scales.DemoAndTest.discreteDomain("cat", catValues),
            yAes to continuousScale, yMinAes to continuousScale, yMaxAes to continuousScale
        )
        val scaleMappersNP: Map<Aes<*>, ScaleMapper<*>> = mapOf()

        val layer = GeomLayerBuilder.demoAndTest(geomProvider, Stats.IDENTITY, PosProvider.wrap(PositionAdjustments.identity()))
            .addBinding(VarBinding(catVar, xAes))
            .addBinding(VarBinding(minVar, yMinAes))
            .addBinding(VarBinding(maxVar, yMaxAes))
            .build(data, scaleByAes, scaleMappersNP)

        return SimplePlotGeomTiles(
            listOf(layer),
            scaleByAes,
            scaleMappersNP,
            CoordProviders.cartesian(),
            containsLiveMap = false
        )
    }

    companion object {
        const val EPSILON = 1e-8
        const val MIN_VALUE = 100.0
        const val MAX_VALUE = 101.0
    }
}