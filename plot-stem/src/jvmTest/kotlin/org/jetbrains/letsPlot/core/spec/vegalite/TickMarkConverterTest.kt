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

class TickMarkConverterTest {
    @Test
    fun simple() {
        val vegaSpec = parseJson(
            """
            {
              "description": "Shows the relationship between horsepower and the number of cylinders using tick marks.",
              "data": {"values": [
                {"cat": 1, "val": 1},
                {"cat": 1, "val": 10.4},
                {"cat": 1, "val": 10.8},
                {"cat": 1, "val": 11.0},
                {"cat": 1, "val": 100},
                {"cat": 2, "val": 1},
                {"cat": 2, "val": 20.4},
                {"cat": 2, "val": 20.8},
                {"cat": 2, "val": 21.0},
                {"cat": 2, "val": 22.5},
                {"cat": 2, "val": 23.0},
                {"cat": 2, "val": 23.5},
                {"cat": 2, "val": 24.0},
                {"cat": 2, "val": 24.5},
                {"cat": 2, "val": 40}
              ]},
              "mark": "tick",
              "encoding": {
                "x": {"field": "val", "type": "quantitative", "scale": {"zero": false}},
                "y": {"field": "cat", "type": "ordinal"}
              }
            }
        """.trimIndent()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(
                PlotBase.DATA, mapOf(
                    "cat" to listOf(1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0),
                    "val" to listOf(1.0, 10.4, 10.8, 11.0, 100.0, 1.0, 20.4, 20.8, 21.0, 22.5, 23.0, 23.5, 24.0, 24.5, 40.0)
                )
            ),
            entry(Layer.GEOM, fromGeomKind(GeomKind.CROSS_BAR)),
            entry(toOption(Aes.SIZE), 0.1),
            entry(toOption(Aes.WIDTH), 0.6),
            entry(PlotBase.MAPPING, mapOf(
                    toOption(Aes.XMIN) to "val",
                    toOption(Aes.XMAX) to "val",
                    toOption(Aes.Y) to "cat"
                )
            ),
            entry(Meta.DATA_META, mapOf(
                Meta.MappingAnnotation.TAG to listOf(
                    mapOf(
                        Meta.MappingAnnotation.AES to toOption(Aes.Y),
                        Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                        Meta.MappingAnnotation.PARAMETERS to mapOf(
                            Meta.MappingAnnotation.LABEL to "cat",
                            Meta.MappingAnnotation.ORDER to 1
                        )
                    ),
                )
            )),
        )
    }
}