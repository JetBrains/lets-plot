/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.vl

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.filterNotNullValues
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.getMaps
import kotlin.test.Test

class SimpleTest {

    @Test
    fun simple() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {"a": "C", "b": 2}, {"a": "C", "b": 7}, {"a": "C", "b": 4},
                |      {"a": "D", "b": 1}, {"a": "D", "b": 2}, {"a": "D", "b": 6},
                |      {"a": "E", "b": 8}, {"a": "E", "b": 4}, {"a": "E", "b": 7}
                |    ]
                |  },
                |  "mark": "point",
                |  "encoding": {
                |    "x": {"field": "a", "type": "nominal"},
                |    "y": {"field": "b", "type": "quantitative"}
                |  }
                |}
        """.trimMargin()
        ).filterNotNullValues() as MutableMap<String, Any>

        SpecTransformBackendUtil.processTransform(vegaSpec).let { spec ->
            assertThat(spec[Meta.KIND])
                .isEqualTo(Meta.Kind.PLOT)

            spec.getMap(PlotBase.DATA)!!.let { data ->
                assertThat(data["a"]).isEqualTo(listOf("C", "C", "C", "D", "D", "D", "E", "E", "E"))
                assertThat(data["b"]).isEqualTo(listOf(2.0, 7.0, 4.0, 1.0, 2.0, 6.0, 8.0, 4.0, 7.0))
            }

            spec.getMaps(Plot.LAYERS)!!.first().let {
                assertThat(it[Layer.GEOM]).isEqualTo(GeomKind.POINT.name.lowercase())
                assertThat(it.getMap(PlotBase.MAPPING)).isEqualTo(
                    mapOf(
                        "x" to "a",
                        "y" to "b"
                    )
                )
            }
        }
    }
}
