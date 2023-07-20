/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import jetbrains.datalore.plot.config.TestUtil.assertClientWontFail
import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.spec.back.ServerSideTestUtil.backendSpecTransform
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

        val opts = parsePlotSpec(spec)
        val opts1 = backendSpecTransform(opts)

        val plotConfigClientSide = assertClientWontFail(opts1)
        assertEquals(1, plotConfigClientSide.layerConfigs.size.toLong())

//        val scale = plotConfigClientSide.scaleMap[Aes.FILL]
        val mapper = plotConfigClientSide.mappersByAesNP.getValue(Aes.FILL)

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
}
