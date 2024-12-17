/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.filterNotNullValues
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.Option.GeomName.fromGeomKind
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.Option.Stat
import org.jetbrains.letsPlot.core.spec.StatKind
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.typed
import org.junit.Test
import java.util.Map.entry

class ScaleTest {

    @Test
    fun `temporal data defines continuous scale`() {
        val vegaSpec = parseJson(
            """
                |{
                |   "data":{
                |     "values":[
                |       { "year":978307200000.0, "net_generation":272, "source":"coal" },
                |       { "year":1009843200000.0, "net_generation":273, "source":"coal" },
                |       { "year":1041379200000.0, "net_generation":287, "source":"coal" },
                |       { "year":1072915200000.0, "net_generation":290, "source":"coal" },
                |       { "year":1104537600000.0, "net_generation":290, "source":"coal" }
                |     ]
                |   },
                |   "mark":{
                |      "type":"line",
                |      "tooltip":{ "content":"encoding" }
                |   },
                |   "encoding":{
                |      "x":{
                |         "field":"year",
                |         "type":"temporal"
                |      },
                |      "y":{
                |         "aggregate":"sum",
                |         "field":"net_generation",
                |         "type":"quantitative"
                |      },
                |      "color":{
                |         "field":"source",
                |         "type":"nominal"
                |      }
                |   },
                |   "params":[
                |      {
                |         "name":"p0",
                |         "select":"interval",
                |         "bind":"scales"
                |      }
                |   ]
                |}           
            """.trimMargin()
        ).filterNotNullValues().asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.LINE)),
            entry(
                PlotBase.MAPPING, mapOf(
                    toOption(Aes.X) to "year",
                    toOption(Aes.Y) to "net_generation",
                    toOption(Aes.COLOR) to "source"
                )),
            entry(PlotBase.DATA, mapOf(
                "..ymax.." to listOf(272.0, 273.0, 287.0, 290.0, 290.0),
                "..ymin.." to listOf(272.0, 273.0, 287.0, 290.0, 290.0),
                "net_generation" to listOf(272.0, 273.0, 287.0, 290.0, 290.0),
                "source" to listOf("coal", "coal", "coal", "coal", "coal"),
                "year" to listOf(9.783072E11, 1.0098432E12, 1.0413792E12, 1.0729152E12, 1.1045376E12)
            )),
            entry(Meta.DATA_META, mapOf(
                Meta.SeriesAnnotation.TAG to listOf(
                    mapOf(
                        Meta.SeriesAnnotation.COLUMN to "year",
                        Meta.SeriesAnnotation.TYPE to Meta.SeriesAnnotation.Types.DATE_TIME
                    )
                )
            )),
            entry(Layer.STAT, StatKind.SUMMARY.name.lowercase()),
            entry(Stat.Summary.FUN, Stat.Summary.Functions.SUM)
        )
    }
}