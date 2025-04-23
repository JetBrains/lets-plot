/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.back.BackendTestUtil.backendSpecTransform
import org.jetbrains.letsPlot.core.spec.config.TestUtil.createPlotConfigFrontend
import org.jetbrains.letsPlot.core.spec.config.TestUtil.assertPlotWontFail
import kotlin.test.Test
import kotlin.test.assertEquals

class DiscreteScaleForContinuousVarTest {
    @Test
    fun `discrete color scale for stat count variable`() {
        val data = "{" +
                "  'time': ['Lunch','Lunch', 'Dinner', 'Dinner', 'Dinner']" +
                "}"

        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data': " + data +
                "           ," +
                "   'mapping': {" +
                "             'x': 'time'," +
                "             'y': '..count..'," +
                "             'fill': '..count..'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'bar'" +
                "                           }" +
                "               }" +
                "           ]" +

                "   ," +
                "   'scales': [" +
                "               {" +
                "                  'aesthetic': 'fill'," +
                "                  'discrete': true," +
                "                  'values': ['#000000','#FFFFFF']" +
                "               }" +
                "           ]" +
                "}"

        val plotSpecsRaw = parsePlotSpec(spec)
        val plotSpecsProcessed = backendSpecTransform(plotSpecsRaw)

        val plotConfigFrontend = createPlotConfigFrontend(plotSpecsProcessed)
        assertEquals(1, plotConfigFrontend.layerConfigs.size.toLong())

        assertPlotWontFail(plotSpecsProcessed)

        val mapperByAes = plotConfigFrontend.createScaleMappers()
        val mapper = mapperByAes.getValue(Aes.FILL)

        // this is discrete scale so input value for mapper is index
        // ..count.. [0] = 2   (two lunched)
        // ..count.. [1] = 3   (three dinners)
//        val color0 = scale.mapper(0.0)
//        val color1 = scale.mapper(1.0)
        val color0 = mapper(0.0)
        val color1 = mapper(1.0)

        assertEquals(Color.BLACK, color0)
        assertEquals(Color.WHITE, color1)
    }


    @Test
    fun `discrete x-axis for numeric 'x' variable`() {
        val spec = """
            
            {'mapping': {},
             'data_meta': {},
             'kind': 'plot',
             'scales': [{'aesthetic': 'x', 'discrete': true, 'reverse': false}],
             'layers': [{'geom': 'point',
               'mapping': {'x': [0, 0.5, 1], 'y': [2, 2, 2], 'color': ['x', 'y', 'z']},
               'data_meta': {},
               'size': 10}],
             'metainfo_list': []}
            
        """.trimIndent()

        val plotSpecsRaw = parsePlotSpec(spec)
        val plotSpecsProcessed = backendSpecTransform(plotSpecsRaw)

        val plotConfigFrontend = createPlotConfigFrontend(plotSpecsProcessed)
        assertEquals(1, plotConfigFrontend.layerConfigs.size.toLong())

        assertPlotWontFail(plotSpecsProcessed)
    }


    @Test
    fun `discrete x-axis for numeric 'xmin','xmax' variables`() {
        val spec = """

            {'mapping': {},
             'data_meta': {},
             'kind': 'plot',
             'scales': [{'aesthetic': 'x', 'discrete': true, 'reverse': false}],
             'layers': [{'geom': 'band',
               'data': {'Brand': ['Subaru', 'Volkswagen', 'AMC'],
                'pos_minx': [-0.5, 2.5, 4.5],
                'pos_maxx': [2.5, 4.5, 7.5],
                'M': ['#41DC8E', '#E0FFFF', '#90D5FF']},
               'mapping': {'xmin': 'pos_minx',
                'xmax': 'pos_maxx',
                'fill': 'Brand',
                'color': 'Brand'},
               'data_meta': {'series_annotations': [{'type': 'str', 'column': 'Brand'},
                 {'type': 'float', 'column': 'pos_minx'},
                 {'type': 'float', 'column': 'pos_maxx'},
                 {'type': 'str', 'column': 'M'}]}}],
             'metainfo_list': []}            
            
        """.trimIndent()

        val plotSpecsRaw = parsePlotSpec(spec)
        val plotSpecsProcessed = backendSpecTransform(plotSpecsRaw)

        val plotConfigFrontend = createPlotConfigFrontend(plotSpecsProcessed)
        assertEquals(1, plotConfigFrontend.layerConfigs.size.toLong())

        assertPlotWontFail(plotSpecsProcessed)
    }
}
