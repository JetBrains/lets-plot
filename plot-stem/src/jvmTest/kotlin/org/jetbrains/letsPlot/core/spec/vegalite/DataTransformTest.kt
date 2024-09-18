/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.filterNotNullValues
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.getMaps
import java.util.Map.entry
import kotlin.test.Test

class DataTransformTest {

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
                |    "x": {"field": "a"},
                |    "y": {"field": "b"}
                |  }
                |}
        """.trimMargin()
        ).filterNotNullValues().asMutable()

        SpecTransformBackendUtil.processTransform(vegaSpec)
            .getMaps(Plot.LAYERS)!!
            .first()
            .getMap(PlotBase.DATA)
            .let {
                assertThat(it).containsExactly(
                    entry("a", listOf("C", "C", "C", "D", "D", "D", "E", "E", "E")),
                    entry("b", listOf(2.0, 7.0, 4.0, 1.0, 2.0, 6.0, 8.0, 4.0, 7.0))
                )
            }
    }
}
