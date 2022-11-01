/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot;

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.random.RandomGaussian
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.interact.NullGeomTargetCollector
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.util.afterOrientation
import jetbrains.datalore.plot.builder.DemoAndTest
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.config.TestUtil
import org.junit.Test
import kotlin.Double.Companion.NaN
import kotlin.math.round
import kotlin.random.Random
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
        val geomLayer = TestUtil.getSingleGeomLayer(plotSpec)
        val geomLayerStub = GeomLayerStub(geomLayer, geomStub)

        // Just check that this invariant still holds.
        assertEquals(
            GeomMeta.renders(GeomKind.BOX_PLOT).toSet(),
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
            targetCollector = NullGeomTargetCollector()
        )

        // Do all checks inside the GeomStub.
        layerRenderer.ensureBuilt()
    }


    companion object {
        private val GEOM_BOUNDS = DoubleRectangle(DoubleVector.ZERO, DoubleVector(100.0, 10.0))

        private val DATA = let {
            val count1 = 50
            val count2 = 100

            val ratingA = gauss(count1, 12, 0.0, 1.0)
            val ratingB = gauss(count2, 24, 0.0, 1.0)
            val rating = ratingA + ratingB
            val cond = List(count1) { "a" } + List(count2) { "b" }

            val map = HashMap<String, List<*>>()
            map["cond"] = cond
            map["rating"] = rating
            map
        }

        private fun gauss(count: Int, seed: Long, mean: Double, stdDeviance: Double): List<Double> {
            val r = RandomGaussian(Random(seed))
            return List(count) { r.nextGaussian() * stdDeviance + mean }
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
        val X: List<Double> = listOf(0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0)
        val Y: List<Double> = listOf(-1.9958, 2.912, -2.4416, NaN, -2.5263, 2.8484, 2.6058, NaN)
        val LOWER: List<Double> = listOf(NaN, NaN, NaN, -0.2541, NaN, NaN, NaN, -0.6743)
        val MIDDLE: List<Double> = listOf(NaN, NaN, NaN, 0.3333, NaN, NaN, NaN, -0.1441)
        val UPPER: List<Double> = listOf(NaN, NaN, NaN, 0.767, NaN, NaN, NaN, 0.5131)
        val YMIN: List<Double> = listOf(NaN, NaN, NaN, -1.7853, NaN, NaN, NaN, -2.3092)
        val YMAX: List<Double> = listOf(NaN, NaN, NaN, 2.083, NaN, NaN, NaN, 2.0206)
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
            assertEquals(ExpectedAes.Y, toList(aesthetics, Aes.Y), message = "Y")
            assertEquals(ExpectedAes.LOWER, toList(aesthetics, Aes.LOWER), message = "LOWER")
            assertEquals(ExpectedAes.MIDDLE, toList(aesthetics, Aes.MIDDLE), message = "MIDDLE")
            assertEquals(ExpectedAes.UPPER, toList(aesthetics, Aes.UPPER), message = "UPPER")
            assertEquals(ExpectedAes.YMIN, toList(aesthetics, Aes.YMIN), message = "YMIN")
            assertEquals(ExpectedAes.YMAX, toList(aesthetics, Aes.YMAX), message = "YMAX")
        }
    }
}
