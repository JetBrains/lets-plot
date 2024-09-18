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
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.typed
import java.util.Map.entry
import kotlin.test.Test

class ErrorBarMarkTransformTest {
    @Test
    fun aggregatedMinMaxHorizontal() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {
                |        "lower_yield": 23.1311,
                |        "upper_yield": 43.5522,
                |        "center": 32.4,
                |        "variety": "Glabron"
                |      },
                |      {
                |        "lower_yield": 23.9503,
                |        "upper_yield": 38.9775,
                |        "center": 30.96667,
                |        "variety": "Manchuria"
                |      },
                |      {
                |        "lower_yield": 24.7778,
                |        "upper_yield": 46.9167,
                |        "center": 33.966665,
                |        "variety": "No. 457"
                |      },
                |      {
                |        "lower_yield": 21.7823,
                |        "upper_yield": 48.9732,
                |        "center": 30.45,
                |        "variety": "No. 462"
                |      }
                |    ]
                |  },
                |  "layer": [
                |    {
                |      "mark": "errorbar",
                |      "encoding": {
                |        "x": {
                |          "field": "upper_yield",
                |          "type": "quantitative",
                |          "scale": {"zero": false},
                |          "title": "yield"
                |        },
                |        "x2": {"field": "lower_yield"},
                |        "y": {"field": "variety", "type": "ordinal"}
                |      }
                |    },
                |    {
                |      "mark": {"type": "point", "filled": true, "color": "black"},
                |      "encoding": {
                |        "x": {"field": "center", "type": "quantitative"},
                |        "y": {"field": "variety", "type": "ordinal"}
                |      }
                |    }
                |  ]
                |}
                """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsExactly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.ERROR_BAR)),
            entry(PlotBase.MAPPING, mapOf(
                toOption(Aes.XMIN) to "upper_yield",
                toOption(Aes.XMAX) to "lower_yield",
                toOption(Aes.Y) to "variety"
            )),
            entry(PlotBase.DATA, emptyMap<String, Any?>()),
        )
    }

    @Test
    fun aggregatedMinMaxVertical() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {
                |        "lower_yield": 23.1311,
                |        "upper_yield": 43.5522,
                |        "center": 32.4,
                |        "variety": "Glabron"
                |      },
                |      {
                |        "lower_yield": 23.9503,
                |        "upper_yield": 38.9775,
                |        "center": 30.96667,
                |        "variety": "Manchuria"
                |      },
                |      {
                |        "lower_yield": 24.7778,
                |        "upper_yield": 46.9167,
                |        "center": 33.966665,
                |        "variety": "No. 457"
                |      },
                |      {
                |        "lower_yield": 21.7823,
                |        "upper_yield": 48.9732,
                |        "center": 30.45,
                |        "variety": "No. 462"
                |      }
                |    ]
                |  },
                |  "layer": [
                |    {
                |      "mark": "errorbar",
                |      "encoding": {
                |        "y": {
                |          "field": "upper_yield",
                |          "type": "quantitative",
                |          "scale": {"zero": false},
                |          "title": "yield"
                |        },
                |        "y2": {"field": "lower_yield"},
                |        "x": {"field": "variety", "type": "ordinal"}
                |      }
                |    },
                |    {
                |      "mark": {"type": "point", "filled": true, "color": "black"},
                |      "encoding": {
                |        "y": {"field": "center", "type": "quantitative"},
                |        "x": {"field": "variety", "type": "ordinal"}
                |      }
                |    }
                |  ]
                |}
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsExactly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.ERROR_BAR)),
            entry(PlotBase.MAPPING, mapOf(
                toOption(Aes.YMIN) to "upper_yield",
                toOption(Aes.YMAX) to "lower_yield",
                toOption(Aes.X) to "variety"
            )),
            entry(PlotBase.DATA, emptyMap<String, Any?>()),
        )
    }


}