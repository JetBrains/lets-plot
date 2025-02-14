/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.Option.GeomName.fromGeomKind
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.BackendTestUtil
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

        val plotSpec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.H_LINE)),
            entry(toOption(Aes.SIZE), 2.0),
            entry(Meta.DATA_META, empty()),
            entry(
                PlotBase.MAPPING, mapOf(
                    toOption(Aes.YINTERCEPT) to "mean_price",
                    toOption(Aes.COLOR) to "symbol",
                )
            ),
            entry(
                PlotBase.DATA, mapOf(
                    "symbol" to listOf("MSFT", "AMZN", "IBM", "GOOG", "AAPL"),
                    "mean_price" to listOf(
                        24.73674796747967,
                        47.98707317073172,
                        91.26121951219511,
                        415.8704411764706,
                        64.73048780487804
                    )
                )
            ),
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

        val plotSpec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.V_LINE)),
            entry(toOption(Aes.SIZE), 2.0),
            entry(Meta.DATA_META, empty()),
            entry(
                PlotBase.MAPPING, mapOf(
                    toOption(Aes.XINTERCEPT) to "mean_price",
                    toOption(Aes.COLOR) to "symbol",
                )
            ),
            entry(
                PlotBase.DATA, mapOf(
                    "symbol" to listOf("MSFT", "AMZN", "IBM", "GOOG", "AAPL"),
                    "mean_price" to listOf(
                        24.73674796747967,
                        47.98707317073172,
                        91.26121951219511,
                        415.8704411764706,
                        64.73048780487804
                    )
                )
            ),
        )
    }

    @Test
    fun vSegment() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {"values": [
                |    {"Origin": "USA", "min_Horsepower": 52, "max_Horsepower": 230},
                |    {"Origin": "Europe", "min_Horsepower": 46, "max_Horsepower": 133},
                |    {"Origin": "Japan", "min_Horsepower": 52, "max_Horsepower": 132}
                |  ]},
                |  "mark": "rule",
                |  "encoding": {
                |    "x": {"field": "Origin"},
                |    "y": {"field": "min_Horsepower", "type": "quantitative"},
                |    "y2": {"field": "max_Horsepower"}
                |  }
                |}
            """.trimMargin()
        ).asMutable()

        val plotSpec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.SEGMENT)),
            entry(Meta.DATA_META, empty()),
            entry(
                PlotBase.MAPPING, mapOf(
                    toOption(Aes.X) to "Origin",
                    toOption(Aes.XEND) to "Origin",
                    toOption(Aes.Y) to "min_Horsepower",
                    toOption(Aes.YEND) to "max_Horsepower"
                )
            ),
            entry(
                PlotBase.DATA, mapOf(
                    "Origin" to listOf("USA", "Europe", "Japan"),
                    "min_Horsepower" to listOf(52.0, 46.0, 52.0),
                    "max_Horsepower" to listOf(230.0, 133.0, 132.0)
                )
            ),
        )
    }

    @Test
    fun hSegment() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {"values": [
                |    {"Origin": "USA", "min_Horsepower": 52, "max_Horsepower": 230},
                |    {"Origin": "Europe", "min_Horsepower": 46, "max_Horsepower": 133},
                |    {"Origin": "Japan", "min_Horsepower": 52, "max_Horsepower": 132}
                |  ]},
                |  "mark": "rule",
                |  "encoding": {
                |    "y": {"field": "Origin"},
                |    "x": {"field": "min_Horsepower", "type": "quantitative"},
                |    "x2": {"field": "max_Horsepower"}
                |  }
                |}
            """.trimMargin()
        ).asMutable()

        val plotSpec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.SEGMENT)),
            entry(
                PlotBase.MAPPING, mapOf(
                    toOption(Aes.Y) to "Origin",
                    toOption(Aes.YEND) to "Origin",
                    toOption(Aes.X) to "min_Horsepower",
                    toOption(Aes.XEND) to "max_Horsepower"
                )
            ),
            entry(
                PlotBase.DATA, mapOf(
                    "Origin" to listOf("USA", "Europe", "Japan"),
                    "min_Horsepower" to listOf(52.0, 46.0, 52.0),
                    "max_Horsepower" to listOf(230.0, 133.0, 132.0)
                )
            ),
            entry(Meta.DATA_META, empty()),
        )
    }
}
