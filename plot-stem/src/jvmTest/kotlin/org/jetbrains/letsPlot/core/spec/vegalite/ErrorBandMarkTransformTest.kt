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
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.typed
import org.junit.Test
import java.util.Map.entry

class ErrorBandMarkTransformTest {

    @Test
    fun aggregatedMinMaxHorizontal() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {"ci1": 23.5007, "ci0": 19.6912, "center": 21.5735, "Year": 189302400000},
                |      {"ci1": 25.8214, "ci0": 20.8554, "center": 23.375, "Year": 220924800000},
                |      {"ci1": 26.4472, "ci0": 21.9749, "center": 24.0611, "Year": 252460800000},
                |      {"ci1": 27.7074, "ci0": 22.6203, "center": 25.0931, "Year": 283996800000}
                |    ]
                |  },
                |  "layer": [
                |    {
                |      "mark": "errorband",
                |      "encoding": {
                |        "y": {
                |          "field": "ci1",
                |          "type": "quantitative",
                |          "scale": {"zero": false},
                |          "title": "Mean of Miles per Gallon (95% CIs)"
                |        },
                |        "y2": {"field": "ci0"},
                |        "x": {"field": "Year", "timeUnit": "year"}
                |      }
                |    },
                |    {
                |      "mark": "line",
                |      "encoding": {
                |        "y": {"field": "center", "type": "quantitative"},
                |        "x": {"field": "Year", "timeUnit": "year"}
                |      }
                |    }
                |  ]
                |}                
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.RIBBON)),
            entry(
                PlotBase.MAPPING, mapOf(
                toOption(Aes.YMIN) to "ci1",
                toOption(Aes.YMAX) to "ci0",
                toOption(Aes.X) to "Year"
            )),
            entry(PlotBase.DATA, mapOf(
                "ci0" to listOf(19.6912, 20.8554, 21.9749, 22.6203),
                "ci1" to listOf(23.5007, 25.8214, 26.4472, 27.7074),
                "Year" to listOf(1.893024E11, 2.209248E11, 2.524608E11, 2.839968E11)
            )),
            entry(Meta.DATA_META, empty()),
        )
    }


    @Test
    fun aggregatedMinMaxVertical() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {"ci1": 23.5007, "ci0": 19.6912, "center": 21.5735, "Year": 189302400000},
                |      {"ci1": 25.8214, "ci0": 20.8554, "center": 23.375, "Year": 220924800000},
                |      {"ci1": 26.4472, "ci0": 21.9749, "center": 24.0611, "Year": 252460800000},
                |      {"ci1": 27.7074, "ci0": 22.6203, "center": 25.0931, "Year": 283996800000}
                |    ]
                |  },
                |  "layer": [
                |    {
                |      "mark": "errorband",
                |      "encoding": {
                |        "x": {
                |          "field": "ci1",
                |          "type": "quantitative",
                |          "scale": {"zero": false},
                |          "title": "Mean of Miles per Gallon (95% CIs)"
                |        },
                |        "x2": {"field": "ci0"},
                |        "y": {"field": "Year", "timeUnit": "year"}
                |      }
                |    },
                |    {
                |      "mark": "line",
                |      "encoding": {
                |        "x": {"field": "center", "type": "quantitative"},
                |        "y": {"field": "Year", "timeUnit": "year"}
                |      }
                |    }
                |  ]
                |}                                
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.RIBBON)),
            entry(PlotBase.DATA, mapOf(
                "ci0" to listOf(19.6912, 20.8554, 21.9749, 22.6203),
                "ci1" to listOf(23.5007, 25.8214, 26.4472, 27.7074),
                "Year" to listOf(1.893024E11, 2.209248E11, 2.524608E11, 2.839968E11)
            )),
            entry(Meta.DATA_META, empty()),
            entry(PlotBase.MAPPING, mapOf(
                toOption(Aes.XMIN) to "ci1",
                toOption(Aes.XMAX) to "ci0",
                toOption(Aes.Y) to "Year"
            )),
        )
    }

}