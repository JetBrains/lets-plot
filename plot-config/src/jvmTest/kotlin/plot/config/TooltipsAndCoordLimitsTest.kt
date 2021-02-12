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
    fun points() {

        val data = "{'x': [1,2,3], 'y': [0,0,0] }"
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
            val withoutCoordLimits = """{ $spec }"""

            val opts = parsePlotSpec(withoutCoordLimits)
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
            val withCoordLimits = """{ 
                $spec, 
                'coord': { 'name': 'cartesian', 'ratio': 1.0, 'xlim': [ 1.1,  2.9 ] }
            }"""
            val opts = parsePlotSpec(withCoordLimits)
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

    companion object {
        private fun assertNoTooltips(tooltipSpecs: List<TooltipSpec>) {
            assertTrue(tooltipSpecs.isEmpty())
        }
        private fun assertTooltips(tooltipSpecs: List<TooltipSpec>) {
            assertEquals(2, tooltipSpecs.size)  // two axis tooltips
        }
    }
}