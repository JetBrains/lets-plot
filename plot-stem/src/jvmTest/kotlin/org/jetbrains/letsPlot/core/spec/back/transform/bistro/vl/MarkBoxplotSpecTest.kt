/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.vl

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.Option.GeomName.fromGeomKind
import org.jetbrains.letsPlot.core.spec.Option.GeomName.fromStatKind
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.StatKind
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.typed
import java.util.Map.entry
import kotlin.test.Test

class MarkBoxplotSpecTest {
    @Test
    fun simple() {
        val vegaSpec = parseJson(
            """
            |{
            |  "data": { "values": [
            |    { "Y": 1.5792128155073915 },
            |    { "Y": 0.7674347291529088 },
            |    { "Y": -0.4694743859349521 },
            |    { "Y": 0.5425600435859647 },
            |    { "Y": -0.46341769281246226 }
            |  ]},
            |  "mark": "boxplot",
            |  "encoding": { "y": { "field": "Y" } }
            |}
        """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsExactly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.BOX_PLOT)),
            entry(PlotBase.DATA, mapOf(
                "..middle.." to listOf(0.5425600435859647),
                "..upper.." to listOf(0.7674347291529088),
                "..x.." to listOf(0.0),
                "..ymax.." to listOf(1.5792128155073915),
                "..lower.." to listOf(-0.46341769281246226),
                "..ymin.." to listOf(-0.4694743859349521)
            )),
            entry(PlotBase.MAPPING, mapOf("y" to "Y")),
        )

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![1].typed<String, Any?>()).containsExactly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(PlotBase.DATA, mapOf(
                "..middle.." to listOf(0.5425600435859647),
                "..upper.." to listOf(0.7674347291529088),
                "..x.." to listOf(0.0),
                "..ymax.." to listOf(1.5792128155073915),
                "..lower.." to listOf(-0.46341769281246226),
                "..ymin.." to listOf(-0.4694743859349521),
                "Y" to listOf(Double.NaN)
            )),
            entry(PlotBase.MAPPING, mapOf("y" to "Y")),
            entry(Layer.STAT, fromStatKind(StatKind.BOXPLOT_OUTLIER)),
        )
    }
}