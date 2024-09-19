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
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.typed
import java.util.Map.entry
import kotlin.test.Test

class RectMarkTransformTest {

    @Test
    fun rectMarkToRectGeom() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {"x_start": 0, "x_end": 10, "y_start": 0, "y_end": 10}
                |    ]
                |  },
                |  "mark": {
                |    "type": "rect"
                |  },
                |  "encoding": {
                |    "x": {
                |      "field": "x_start"
                |    },
                |    "x2": {
                |      "field": "x_end"
                |    },
                |    "y": {
                |      "field": "y_start"
                |    },
                |    "y2": {
                |      "field": "y_end"
                |    }
                |  }
                |}
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsExactly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.RECT)),
            entry(
                PlotBase.DATA, mapOf(
                    "x_end" to listOf(10.0),
                    "y_end" to listOf(10.0),
                    "y_start" to listOf(0.0),
                    "x_start" to listOf(0.0),
                )
            ),
            entry(
                PlotBase.MAPPING, mapOf(
                    toOption(Aes.YMIN) to "y_start",
                    toOption(Aes.YMAX) to "y_end",
                    toOption(Aes.XMIN) to "x_start",
                    toOption(Aes.XMAX) to "x_end",
                )
            ),
        )
    }
}