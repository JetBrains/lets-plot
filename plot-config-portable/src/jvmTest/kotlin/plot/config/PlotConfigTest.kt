/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.config.Option.Plot.SCALES
import jetbrains.datalore.plot.config.Option.Scale.CONTINUOUS_TRANSFORM
import jetbrains.datalore.plot.config.Option.Scale.NAME
import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import kotlin.test.Test
import kotlin.test.assertEquals

class PlotConfigTest {
    @Test
    fun scaleOptionsAreMergedForTheSameAes() {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'scales': [" +
                "               {" +
                "                  'aesthetic': 'x'," +
                "                  'name': 'name_test'" +
                "               }" +
                "               ," +
                "               {" +
                "                  'aesthetic': 'x'," +
                "                  'trans': 'log10'" +
                "               }" +
                "           ]" +
                "}"

        val opts = parsePlotSpec(spec)
        val plotConfig = PlotConfigFrontend.create(opts) {}

        val scaleConfigs = PlotConfigUtil.createScaleConfigs(plotConfig.getList(SCALES))
        assertEquals(1, scaleConfigs.size.toLong())
        assertEquals("name_test", scaleConfigs[0].getString(NAME))
        assertEquals("log10", scaleConfigs[0].getString(CONTINUOUS_TRANSFORM))
    }
}