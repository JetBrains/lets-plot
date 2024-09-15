/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.vl

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.GeomName.fromGeomKind
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.getMaps
import kotlin.test.Test

class BoxplotSpecTest {
    @Test
    fun simple() {
        val vegaSpec = parseJson("""
            |{
            |  "description": "A horizontal box plot showing median and lower and upper quartiles of the distribution of body mass of penguins.",
            |  "data": {"url": "data/penguins.json"},
            |  "mark": "boxplot",
            |  "encoding": {
            |    "x": {
            |      "field": "Body Mass (g)",
            |      "type": "quantitative",
            |      "scale": {"zero": false}
            |    }
            |  }
            |}
        """.trimMargin()).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)
        plotSpec.getMaps(Plot.LAYERS)!!.first().let {
            assertThat(it[Layer.GEOM]).isEqualTo(fromGeomKind(GeomKind.BOX_PLOT))
            assertThat(it.getMap(Option.PlotBase.MAPPING))
                .isEqualTo(mapOf("x" to "Body Mass (g)"))
        }
        //assertThat(plotSpec.getMap(Option.PlotBase.DATA))
        //    .isEqualTo(mapOf("a" to listOf(1.0)))
    }
}