/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.DemoAndTest
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.parsePlotSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TooltipsAndCoordLimitsTest {

    @Test
    fun `geom points`() {

        val data = "{'x': [1, 2, 3], 'y': [0, 0, 0] }"
        val spec = """
            'kind': 'plot',
            'ggsize': { 'width': 400, 'height': 200 },
            'data': $data,
            'layers': [
                         {
                            'geom':  { 'name': 'point' },
                            'mapping': { 'x': 'x', 'y': 'y' }
                          }
                      ]
        """.trimIndent()

        run {
            val opts = parsePlotSpec("{ $spec }")
            val plot = DemoAndTest.createPlot(opts)
            val geomBounds = plot.getGeomBounds(DoubleVector(200.0, 100.0))
            assertNotNull(geomBounds)

            // point #1 has tooltips
            val tooltipSpec1 = plot.createTooltipSpecs(DoubleVector(80.0, 180.0))
            assertTooltips(tooltipSpec1)

            // point #2 has tooltips
            val tooltipSpec2 = plot.createTooltipSpecs(DoubleVector(300.0, 180.0))
            assertTooltips(tooltipSpec2)

            // point #3 has tooltips
            val tooltipSpec3 = plot.createTooltipSpecs(DoubleVector(550.0, 180.0))
            assertTooltips(tooltipSpec3)
        }

        run {
            val fullSpec = """{ 
                $spec, 
                'coord': { 'name': 'cartesian', 'xlim': [ 1.1,  2.9 ] }
            }"""
            val opts = parsePlotSpec(fullSpec)
            val plot = DemoAndTest.createPlot(opts)
            val geomBounds = plot.getGeomBounds(DoubleVector(200.0, 100.0))
            assertNotNull(geomBounds)

            // point #1 is outside => no tooltips
            val tooltipSpec1 = plot.createTooltipSpecs(DoubleVector(80.0, 180.0))
            assertNoTooltips(tooltipSpec1)

            // point #2 has tooltips
            val tooltipSpec2 = plot.createTooltipSpecs(DoubleVector(300.0, 180.0))
            assertTooltips(tooltipSpec2)

            // point $3 is outside => no tooltips
            val tooltipSpec3 = plot.createTooltipSpecs(DoubleVector(550.0, 180.0))
            assertNoTooltips(tooltipSpec3)
        }
    }

    @Test
    fun `path - at least one point should be within limits`() {
        val data = "{'x': [1, 2, 3], 'y': [0, 0, 0] }"
        val spec = """
            'kind': 'plot',
            'ggsize': { 'width': 400, 'height': 200 },
            'data': $data,
            'layers': [
                         {
                            'geom':  { 'name': 'line' },
                            'mapping': { 'x': 'x', 'y': 'y' }
                          }
                      ]
        """.trimIndent()

        run {
            // points are within xlim
            val fullSpec = """{ 
                $spec, 
                'coord': { 'name': 'cartesian', 'xlim': [ 1,  2 ] }
            }"""
            val opts = parsePlotSpec(fullSpec)
            val plot = DemoAndTest.createPlot(opts)

            val geomBounds = plot.getGeomBounds(DoubleVector(200.0, 100.0))
            assertNotNull(geomBounds)

            assertTooltips(plot.createTooltipSpecs(geomBounds.center))
        }

        run {
            // outside
            val fullSpec = """{ 
                $spec, 
                'coord': { 'name': 'cartesian', 'xlim': [ 0,  0.5 ] }
            }"""
            val opts = parsePlotSpec(fullSpec)
            val plot = DemoAndTest.createPlot(opts)

            val geomBounds = plot.getGeomBounds(DoubleVector(200.0, 100.0))
            assertNotNull(geomBounds)

            assertNoTooltips(plot.createTooltipSpecs(geomBounds.center))
        }
    }

    @Test
    fun `polygon - bbox should be inside limits`() {
        val data = "{'x': [0, 1, 2, 3], 'y': [0, 2, 3, 1] }"
        val spec = """
            'kind': 'plot',
            'ggsize': { 'width': 400, 'height': 200 },
            'data': $data,
            'layers': [
                         {
                            'geom':  { 'name': 'polygon' },
                            'mapping': { 'x': 'x', 'y': 'y' },
                            'tooltips': {'tooltip_lines': ['^x']}
                          }
                      ]
        """.trimIndent()

        run {
            // bbox is inside limits
            val fullSpec = """{ 
                $spec, 
                'coord': { 'name': 'cartesian', 'xlim': [ 0,  4] }
            }"""
            val opts = parsePlotSpec(fullSpec)
            val plot = DemoAndTest.createPlot(opts)

            val geomBounds = plot.getGeomBounds(DoubleVector(200.0, 100.0))
            assertNotNull(geomBounds)

            assertTooltips(plot.createTooltipSpecs(geomBounds.center))
        }

        run {
            // outside
            val fullSpec = """{ 
                $spec, 
                'coord': { 'name': 'cartesian', 'xlim': [ 0,  2] }
            }"""
            val opts = parsePlotSpec(fullSpec)
            val plot = DemoAndTest.createPlot(opts)

            val geomBounds = plot.getGeomBounds(DoubleVector(200.0, 100.0))
            assertNotNull(geomBounds)

            assertNoTooltips(plot.createTooltipSpecs(geomBounds.center))
        }
    }

    @Test
    fun `rectangle - all vertices should be inside limits`() {
        val spec = """
            'kind': 'plot',
            'ggsize': { 'width': 400, 'height': 200 },
            'layers': [
                         {
                            'geom':  { 'name': 'rect' },
                            'mapping': { 'xmin': [0], 'xmax': [3], 'ymin': [0], 'ymax': [3] },
                            'tooltips': {'tooltip_lines': ['^xmin, ^xmax']}
                          }
                      ]
        """.trimIndent()

        run {
            // inside
            val opts = parsePlotSpec("{ $spec }")
            val plot = DemoAndTest.createPlot(opts)

            val geomBounds = plot.getGeomBounds(DoubleVector(200.0, 100.0))
            assertNotNull(geomBounds)

            assertTooltips(plot.createTooltipSpecs(geomBounds.center))
        }

        run {
            // outside
            val fullSpec = """{ 
                $spec, 
                'coord': { 'name': 'cartesian', 'ylim': [ 1,  2] }
            }"""
            val opts = parsePlotSpec(fullSpec)
            val plot = DemoAndTest.createPlot(opts)

            val geomBounds = plot.getGeomBounds(DoubleVector(200.0, 100.0))
            assertNotNull(geomBounds)

            assertNoTooltips(plot.createTooltipSpecs(geomBounds.center))
        }
    }

    companion object {
        private fun assertNoTooltips(tooltipSpecs: List<TooltipSpec>) {
            assertTrue(tooltipSpecs.isEmpty())
        }
        private fun assertTooltips(tooltipSpecs: List<TooltipSpec>) {
            assertTrue(tooltipSpecs.isNotEmpty())
        }
    }
}