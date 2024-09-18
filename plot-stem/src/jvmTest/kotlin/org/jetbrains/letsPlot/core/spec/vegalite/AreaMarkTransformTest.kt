/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.Option.GeomName.fromGeomKind
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.typed
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
            .containsExactly(
                entry(Layer.GEOM, fromGeomKind(GeomKind.AREA)),
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

}