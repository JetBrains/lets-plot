/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble.tiles

import demoAndTestShared.assertEquals
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.Geom
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.geom.ErrorBarGeom
import org.jetbrains.letsPlot.core.plot.base.pos.PositionAdjustments
import org.jetbrains.letsPlot.core.plot.base.scale.Scales
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.assemble.GeomLayerBuilder
import org.jetbrains.letsPlot.core.plot.builder.assemble.PosProvider
import org.jetbrains.letsPlot.core.plot.builder.assemble.geom.GeomProvider
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProviders
import kotlin.test.Test

class SimplePlotGeomTilesTest {
    @Test
    fun testErrorBarGeomCalculatesYRangeCorrectly() {
        val xVar = DataFrame.Variable("x")
        val yMinVar = DataFrame.Variable("ymin")
        val yMaxVar = DataFrame.Variable("ymax")
        val xs = listOf("a")
        val yMin = 100.0
        val yMax = 101.0
        val yMins = listOf(yMin)
        val yMaxs = listOf(yMax)

        val supplier: (GeomProvider.Context) -> Geom = { ErrorBarGeom(isVertical = true) }
        val geomProvider = GeomProvider.errorBar(supplier)
        val stat = Stats.IDENTITY
        val posProvider = PosProvider.wrap(PositionAdjustments.identity())
        val data = DataFrame.Builder()
            .put(xVar, xs)
            .put(yMinVar, yMins)
            .put(yMaxVar, yMaxs)
            .build()
        val yScale = Scales.DemoAndTest.continuousDomain("y", Aes.Y)
        val scaleByAes = mapOf<Aes<*>, Scale>(
            Aes.X to Scales.DemoAndTest.discreteDomain("x", xs),
            Aes.Y to yScale, Aes.YMIN to yScale, Aes.YMAX to yScale
        )
        val scaleMappersNP: Map<Aes<*>, ScaleMapper<*>> = mapOf()

        val layer = GeomLayerBuilder.demoAndTest(geomProvider, stat, posProvider)
            .addBinding(VarBinding(xVar, Aes.X))
            .addBinding(VarBinding(yMinVar, Aes.YMIN))
            .addBinding(VarBinding(yMaxVar, Aes.YMAX))
            .build(data, scaleByAes, scaleMappersNP)

        val geomTiles = SimplePlotGeomTiles(
            listOf(layer),
            scaleByAes,
            scaleMappersNP,
            CoordProviders.cartesian(),
            containsLiveMap = false
        )

        val (_, yDomain) = geomTiles.overallXYContinuousDomains()
        assertEquals(yMin - 0.05, yDomain?.lowerEnd, EPSILON)
        assertEquals(yMax + 0.05, yDomain?.upperEnd, EPSILON)
    }

    companion object {
        const val EPSILON = 1e-8
    }
}