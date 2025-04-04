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
        val catValues = listOf("a")
        val xVar = DataFrame.Variable("cat")
        val yMinVar = DataFrame.Variable("ymin")
        val yMaxVar = DataFrame.Variable("ymax")

        val geomProvider = GeomProvider.errorBar { ErrorBarGeom() }
        val data = DataFrame.Builder()
            .put(xVar, catValues)
            .put(yMinVar, listOf(MIN_VALUE))
            .put(yMaxVar, listOf(MAX_VALUE))
            .build()
        val continuousScale = Scales.DemoAndTest.continuousDomain(Aes.Y.name, Aes.Y)
        val scaleByAes = mapOf<Aes<*>, Scale>(
            Aes.X to Scales.DemoAndTest.discreteDomain("cat", catValues),
            Aes.Y to continuousScale, Aes.YMIN to continuousScale, Aes.YMAX to continuousScale
        )
        val scaleMappersNP: Map<Aes<*>, ScaleMapper<*>> = mapOf()

        val layer = GeomLayerBuilder.demoAndTest(geomProvider, Stats.IDENTITY, PosProvider.wrap(PositionAdjustments.identity()))
            .addBinding(VarBinding(xVar, Aes.X))
            .addBinding(VarBinding(yMinVar, Aes.YMIN))
            .addBinding(VarBinding(yMaxVar, Aes.YMAX))
            .build(data, scaleByAes, scaleMappersNP)

        val errorBarTile = SimplePlotGeomTiles(
            listOf(layer),
            scaleByAes,
            scaleMappersNP,
            CoordProviders.cartesian(),
            containsLiveMap = false
        )

        val (_, yDomain) = errorBarTile.overallXYContinuousDomains()
        assertEquals(MIN_VALUE - 0.05, yDomain?.lowerEnd, EPSILON)
        assertEquals(MAX_VALUE + 0.05, yDomain?.upperEnd, EPSILON)
    }

    companion object {
        const val EPSILON = 1e-8
        const val MIN_VALUE = 100.0
        const val MAX_VALUE = 101.0
    }
}