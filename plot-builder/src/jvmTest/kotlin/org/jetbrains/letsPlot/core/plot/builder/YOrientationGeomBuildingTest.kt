/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import demoAndTestShared.TestingGeomLayersBuilder
import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.random.RandomGaussian.Companion.normal
import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.NullGeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.util.afterOrientation
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProviders
import org.junit.Test
import kotlin.math.round
import kotlin.test.assertEquals

/**
 * See: related demo: BoxPlotJfx.kt
 */
class YOrientationGeomBuildingTest {
    @Test
    fun test() {
        check(createPlotSpec(yOrientation = false), GeomStub(yOrientation = false))
        check(createPlotSpec(yOrientation = true), GeomStub(yOrientation = true))
    }

    private fun check(plotSpec: MutableMap<String, Any>, geomStub: GeomStub) {
        val geomLayer = TestingGeomLayersBuilder.getSingleGeomLayer(plotSpec)
        val geomLayerStub = GeomLayerStub(geomLayer, geomStub)

        // Just check that this invariant still holds.
        assertEquals(
            GeomMeta.renders(GeomKind.BOX_PLOT, Aes.COLOR, Aes.FILL).toSet(),
            geomLayerStub.renderedAes().toSet()
        )

        val xAxisLength = GEOM_BOUNDS.width
        val yAxisLength = GEOM_BOUNDS.height
        val xDomain = GEOM_BOUNDS.xRange()
        val yDomain = GEOM_BOUNDS.yRange()
        val layerRenderer = DemoAndTest.buildGeom(
            layer = geomLayerStub,
            xyAesBounds = DoubleRectangle(DoubleVector.ZERO, DoubleVector(xAxisLength, yAxisLength)),
            coord = CoordProviders.cartesian().let {
                val adjustedDomain = it.adjustDomain(DoubleRectangle(xDomain, yDomain))
                it.createCoordinateSystem(
                    adjustedDomain = adjustedDomain,
                    clientSize = DoubleVector(xAxisLength, yAxisLength)
                )
            },
            flippedAxis = false,
            targetCollector = NullGeomTargetCollector(),
            plotBackground = Color.WHITE
        )

        // Do all checks inside the GeomStub.
        layerRenderer.ensureBuilt()
    }


    companion object {
        private val GEOM_BOUNDS = DoubleRectangle(DoubleVector.ZERO, DoubleVector(100.0, 10.0))

        private val DATA = let {
            val count1 = 50
            val count2 = 100

            val ratingA = normal(count1, 0.0, 1.0, 12)
            val ratingB = normal(count2, 0.0, 1.0, 24)
            val rating = ratingA + ratingB
            val cond = List(count1) { "a" } + List(count2) { "b" }

            val map = HashMap<String, List<*>>()
            map["cond"] = cond
            map["rating"] = rating
            map
        }

        private fun createPlotSpec(yOrientation: Boolean): MutableMap<String, Any> {
            val spec = """
                {
                    'kind': 'plot',
                    'mapping': {
                        '${Aes.X.afterOrientation(yOrientation).name}': 'cond',
                        '${Aes.Y.afterOrientation(yOrientation).name}': 'rating'
                    },    
                    'layers': [
                        {
                            ${if (yOrientation) "'orientation': 'y'," else ""}
                            'geom': 'boxplot' 
                        }
                    ]
                }
            """.trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }
    }

    private object ExpectedAes {
        val X: List<Double> = listOf(0.0, 1.0)
        val LOWER: List<Double> = listOf(-0.2541, -0.6743)
        val MIDDLE: List<Double> = listOf(0.3333, -0.1441)
        val UPPER: List<Double> = listOf(0.767, 0.5131)
        val YMIN: List<Double> = listOf(-1.7853, -2.3092)
        val YMAX: List<Double> = listOf(2.083, 2.0206)
    }

    private class GeomLayerStub(
        actualLayer: GeomLayer,
        val geomStub: Geom
    ) : GeomLayer by actualLayer {
        override val geom: Geom
            get() = geomStub
    }

    private class GeomStub(
        val yOrientation: Boolean
    ) : Geom {
        override val legendKeyElementFactory: LegendKeyElementFactory
            get() = UNSUPPORTED()

        override fun build(
            root: SvgRoot,
            aesthetics: Aesthetics,
            pos: PositionAdjustment,
            coord: CoordinateSystem,
            ctx: GeomContext
        ) {
            // ctx
            assertEquals(GEOM_BOUNDS, ctx.getAesBounds())
            assertEquals(yOrientation, ctx.flipped)


            // aesthetics

            fun toList(aesthetics: Aesthetics, aes: Aes<Double>): List<Double> {
                return aesthetics.numericValues(aes).filterNotNull()
                    .map {
                        round(it * 10000) / 10000.0
                    }
            }

            assertEquals(ExpectedAes.X, toList(aesthetics, Aes.X), message = "X")
            assertEquals(ExpectedAes.LOWER, toList(aesthetics, Aes.LOWER), message = "LOWER")
            assertEquals(ExpectedAes.MIDDLE, toList(aesthetics, Aes.MIDDLE), message = "MIDDLE")
            assertEquals(ExpectedAes.UPPER, toList(aesthetics, Aes.UPPER), message = "UPPER")
            assertEquals(ExpectedAes.YMIN, toList(aesthetics, Aes.YMIN), message = "YMIN")
            assertEquals(ExpectedAes.YMAX, toList(aesthetics, Aes.YMAX), message = "YMAX")
        }
    }
}
