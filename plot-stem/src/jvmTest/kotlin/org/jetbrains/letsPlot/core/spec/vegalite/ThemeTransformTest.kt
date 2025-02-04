/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.junit.Test
import java.util.Map.entry

class ThemeTransformTest {

    @Test
    fun `fill and color should produce same title to combine legend into one`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {"values": [{"symbol": "MSFT", "price": 39.81}]},
                |  "layer": [
                |    {
                |      "mark": {"type": "bar", "color": "green"},
                |      "encoding": {
                |        "x": {"field": "symbol", "type": "nominal", "title": "Stock Symbol"},
                |        "y": { "aggregate": "mean", "field": "price", "type": "quantitative", "title": "Average Price" }, 
                |        "color": {"field": "symbol", "type": "nominal", "title": "Stock Symbol"}
                |      }
                |    }
                |  ]
                |}                
        """.trimMargin()
        ).asMutable()

        val spec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(spec).contains(
            entry(Option.Plot.GUIDES, mapOf(
                toOption(Aes.X) to mapOf(Option.Guide.TITLE to "Stock Symbol"),
                toOption(Aes.Y) to mapOf(Option.Guide.TITLE to "Average Price"),
                toOption(Aes.FILL) to mapOf(Option.Guide.TITLE to "Stock Symbol"),
                toOption(Aes.COLOR) to mapOf(Option.Guide.TITLE to "Stock Symbol")
            ))
        )
    }

    @Test
    fun `axis title`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "mark": "point",
                |  
                |  "data": { "values": [ { "a": 1, "b": 2 } ]  },
                |  "encoding": { 
                |    "x": { "field": "a", "title": "A axis" },
                |    "y": { "field": "b", "title": "B axis" }
                |  }
                |}
        """.trimMargin()
        ).asMutable()

        val spec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(spec).contains(
            entry(Option.Plot.GUIDES, mapOf(
                toOption(Aes.X) to mapOf(Option.Guide.TITLE to "A axis"),
                toOption(Aes.Y) to mapOf(Option.Guide.TITLE to "B axis")
            ))
        )

    }
}