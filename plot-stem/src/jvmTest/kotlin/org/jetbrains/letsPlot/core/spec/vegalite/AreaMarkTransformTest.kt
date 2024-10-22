/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.*
import org.jetbrains.letsPlot.core.spec.Option.GeomName.fromGeomKind
import org.jetbrains.letsPlot.core.spec.Option.GeomName.fromStatKind
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.Option.Pos
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.junit.Test
import java.util.Map.entry

class AreaMarkTransformTest {
    @Test
    fun typeAsString() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |      "values": [
                |      {"u": 1, "v": 28},
                |      {"u": 2, "v": 55},
                |      {"u": 3, "v": 42},
                |      {"u": 4, "v": 34},
                |      {"u": 5, "v": 36},
                |      {"u": 6, "v": 48}
                |      ]
                |  },
                |  "mark": {
                |      "type": "area"
                |  },
                |  "encoding": {
                |      "x": {"field": "u", "type": "quantitative"},
                |      "y": {"field": "v", "type": "quantitative"}
                |  }
                |}"""
                .trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)
        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>())
            .containsOnly(
                entry(Layer.GEOM, fromGeomKind(GeomKind.AREA)),
                entry(Option.Meta.DATA_META, empty()),
                entry(
                    PlotBase.DATA, mapOf(
                        "u" to listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0),
                        "v" to listOf(28.0, 55.0, 42.0, 34.0, 36.0, 48.0)
                    )
                ),
                entry(
                    PlotBase.MAPPING, mapOf(
                        "x" to "u",
                        "y" to "v"
                    )
                )
            )
    }

    @Test
    fun densityTransform() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {"v": 1}, {"v": 1}, {"v": 1}, {"v": 1}, {"v": 1}, {"v": 1}, 
                |      {"v": 2}, {"v": 2}, {"v": 2},
                |      {"v": 3}, {"v": 3}, {"v": 3}, {"v": 3}, {"v": 3}, {"v": 3}, {"v": 3}, {"v": 3}, {"v": 3}, {"v": 3}, {"v": 3},
                |      {"v": 4}, {"v": 4}, {"v": 4}
                |    ]
                |  },
                |  "transform":[{
                |    "density": "v",
                |    "bandwidth": 0.3
                |  }],
                |  "mark": "area",
                |  "encoding": {
                |    "x": {
                |      "field": "value",
                |      "type": "quantitative"
                |    },
                |    "y": {
                |      "field": "density",
                |      "type": "quantitative"
                |    }
                |  }
                |}
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMap(Plot.LAYERS, 0)!! - PlotBase.DATA).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.DENSITY)),
            entry(Layer.STAT, fromStatKind(StatKind.DENSITY)),
            entry(Layer.POS, mapOf(Pos.NAME to PosProto.STACK)),
            entry(Option.Meta.DATA_META, empty()),
            entry(PlotBase.MAPPING, mapOf(
                toOption(Aes.X) to "v",
                toOption(Aes.Y) to "..density.."
            )),
        )
    }

}