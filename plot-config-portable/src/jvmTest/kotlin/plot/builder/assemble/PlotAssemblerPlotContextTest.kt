/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import jetbrains.datalore.plot.config.TestUtil.buildGeomLayer
import kotlin.test.Test
import kotlin.test.assertEquals
import org.jetbrains.letsPlot.core.plot.builder.assemble.TestingPlotContext


class PlotAssemblerPlotContextTest {

    @Test
    fun `boxplot norm-orientation`() {
        val geomLayer = buildGeomLayer(
            geom = "boxplot",
            data = mapOf<String, List<Any>>(
                "alphabet" to listOf("a", "a", "b", "a", "a", "a", "b", "b", "b", "a", "a", "a"),
                "coeff" to listOf(119, 289, 387, 491, 588, 694, 791, 888, 994, 0.191, 988, 994)
            ),
            mapping = mapOf(
                Aes.X.name to "alphabet",
                Aes.Y.name to "coeff"
            )
        )

        val ctx = TestingPlotContext.create(geomLayer)
        assertEquals(DoubleSpan(0.0, 1.0), ctx.overallTransformedDomain(Aes.X)) // discrete
        assertEquals(DoubleSpan(0.191, 994.0), ctx.overallTransformedDomain(Aes.Y)) // continuous
    }

    @Test
    fun `boxplot with y-orientation`() {
        val geomLayer = buildGeomLayer(
            geom = "boxplot",
            data = mapOf<String, List<Any>>(
                "alphabet" to listOf("a", "a", "b", "a", "a", "a", "b", "b", "b", "a", "a", "a"),
                "coeff" to listOf(119, 289, 387, 491, 588, 694, 791, 888, 994, 0.191, 988, 994)
            ),
            mapping = mapOf(
                Aes.X.name to "coeff",
                Aes.Y.name to "alphabet",
            ),
            orientationY = true
        )

        val ctx = TestingPlotContext.create(geomLayer)
        assertEquals(DoubleSpan(0.191, 994.0), ctx.overallTransformedDomain(Aes.X)) // continuous
        assertEquals(DoubleSpan(0.0, 1.0), ctx.overallTransformedDomain(Aes.Y)) // discrete
    }

    @Test
    fun `boxplot x,y-numeric, norm-orientation`() {
        val geomLayer = buildGeomLayer(
            geom = "boxplot",
            data = mapOf<String, List<Any>>(
                "horizontal" to listOf(119, 289, 387, 491, 588, 694, 791, 888, 994, 0.191, 988, 994),
                "vertical" to listOf(
                    0.119,
                    0.289,
                    0.387,
                    0.491,
                    0.588,
                    0.694,
                    0.791,
                    0.888,
                    0.994,
                    0.0191,
                    0.988,
                    0.994
                )
            ),
            mapping = mapOf(
                Aes.Y.name to "vertical"
            )
        )

        val ctx = TestingPlotContext.create(geomLayer)
        assertEquals(DoubleSpan(-0.5, 0.5), ctx.overallTransformedDomain(Aes.X)) // 1.0 +-0.5
        assertEquals(DoubleSpan(0.0191, 0.994), ctx.overallTransformedDomain(Aes.Y)) // continuous
    }

    @Test
    fun `boxplot x,y-numeric, Y-orientation`() {
        val geomLayer = buildGeomLayer(
            geom = "boxplot",
            data = mapOf<String, List<Any>>(
                "horizontal" to listOf(119, 289, 387, 491, 588, 694, 791, 888, 994, 0.191, 988, 994),
                "vertical" to listOf(
                    0.119,
                    0.289,
                    0.387,
                    0.491,
                    0.588,
                    0.694,
                    0.791,
                    0.888,
                    0.994,
                    0.0191,
                    0.988,
                    0.994
                )
            ),
            mapping = mapOf(
                Aes.X.name to "horizontal"
            ),
            orientationY = true
        )

        val ctx = TestingPlotContext.create(geomLayer)
        assertEquals(DoubleSpan(0.191, 994.0), ctx.overallTransformedDomain(Aes.X)) // continuous
        assertEquals(DoubleSpan(-0.5, 0.5), ctx.overallTransformedDomain(Aes.Y)) // 1 +-0.5
    }

    @Test
    fun `boxplot x,y-numeric, 2 layers, alternate orientation`() {
        val data = mapOf<String, List<Any>>(
            "horizontal" to listOf(119, 289, 387, 491, 588, 694, 791, 888, 994, 0.191, 988, 994),
            "vertical" to listOf(0.119, 0.289, 0.387, 0.491, 0.588, 0.694, 0.791, 0.888, 0.994, 0.0191, 0.988, 0.994)
        )

        // Normal orientation layer
        val geomLayerNorm = buildGeomLayer(
            geom = "boxplot",
            data = data,
            mapping = mapOf(
                Aes.Y.name to "vertical"
            )
        )

        // Y-orientation layer
        val geomLayerY = buildGeomLayer(
            geom = "boxplot",
            data = data,
            mapping = mapOf(
                Aes.X.name to "horizontal"
            ),
            orientationY = true
        )

        val ctx = TestingPlotContext.create(listOf(geomLayerNorm, geomLayerY), geomLayerNorm.scaleMap)
//        assertEquals(DoubleSpan(0.191, 994.0), ctx.overallTransformedDomain(Aes.X)) // continuous
        // x-span starts at 0.0 because 'normal' boxplot's X == 0.0
        assertEquals(DoubleSpan(0.0, 994.0), ctx.overallTransformedDomain(Aes.X))
        // y-span starts at 0.0 because 'y-oriented' boxplot's Y == 0.0
        assertEquals(DoubleSpan(0.0, 0.994), ctx.overallTransformedDomain(Aes.Y))
    }

}