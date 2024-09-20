/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.GeomName.fromGeomKind
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.typed
import org.junit.Test
import java.util.Map.entry

class RuleMarkTransformTest {

    @Test
    fun hLine() {
        val vegaSpec = parseJson(
            """
                |{
                |  "description": "Average Stock prices of 5 Tech Companies.",
                |  "data": {
                |    "values": [
                |      {"symbol": "MSFT", "mean_price": 24.73674796747967},
                |      {"symbol": "AMZN", "mean_price": 47.98707317073172},
                |      {"symbol": "IBM", "mean_price": 91.26121951219511},
                |      {"symbol": "GOOG", "mean_price": 415.8704411764706},
                |      {"symbol": "AAPL", "mean_price": 64.73048780487804}
                |    ]
                |  },
                |  "mark": "rule",
                |  "encoding": {
                |    "y": {"field": "mean_price", "type": "quantitative"},
                |    "size": {"value": 2},
                |    "color": {"field": "symbol", "type": "nominal"}
                |  }
                |}                
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.H_LINE)),
            entry(
                Option.PlotBase.MAPPING, mapOf(
                    "y" to "mean_price",
                    "color" to "symbol",
                )
            ),
            entry(
                Option.PlotBase.DATA, mapOf(
                    "symbol" to listOf("MSFT", "AMZN", "IBM", "GOOG", "AAPL")
                )
            )
        )
    }

    @Test
    fun vLine() {
        val vegaSpec = parseJson(
            """
                |{
                |  "description": "Average Stock prices of 5 Tech Companies.",
                |  "data": {
                |    "values": [
                |      {"symbol": "MSFT", "mean_price": 24.73674796747967},
                |      {"symbol": "AMZN", "mean_price": 47.98707317073172},
                |      {"symbol": "IBM", "mean_price": 91.26121951219511},
                |      {"symbol": "GOOG", "mean_price": 415.8704411764706},
                |      {"symbol": "AAPL", "mean_price": 64.73048780487804}
                |    ]
                |  },
                |  "mark": "rule",
                |  "encoding": {
                |    "x": {"field": "mean_price", "type": "quantitative"},
                |    "size": {"value": 2},
                |    "color": {"field": "symbol", "type": "nominal"}
                |  }
                |}                
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.V_LINE)),
            entry(
                Option.PlotBase.MAPPING, mapOf(
                    "x" to "mean_price",
                    "color" to "symbol",
                )
            ),
            entry(
                Option.PlotBase.DATA, mapOf(
                    "symbol" to listOf("MSFT", "AMZN", "IBM", "GOOG", "AAPL")
                )
            )
        )
    }

    @Test
    fun segment() {
        val vegaSpec = parseJson(
            """
                |{
                |  "description": "Average Stock prices of 5 Tech Companies.",
                |  "data": {
                |    "values": [
                |      {"x_start": 1}, {"y_start": 2}, {"x_end": 3}, {"y_end": 4}
                |    ]
                |  },
                |  "mark": "rule",
                |  "encoding": {
                |    "x": {"field": "x_start"},
                |    "y": {"field": "y_start"},
                |    "x2": {"field": "x_end"},
                |    "y2": {"field": "y_end"},
                |  }
                |}                
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.V_LINE)),
            entry(
                Option.PlotBase.MAPPING, mapOf(
                    "x" to "mean_price",
                    "color" to "symbol",
                )
            ),
            entry(
                Option.PlotBase.DATA, mapOf(
                    "symbol" to listOf("MSFT", "AMZN", "IBM", "GOOG", "AAPL")
                )
            )
        )
    }

}