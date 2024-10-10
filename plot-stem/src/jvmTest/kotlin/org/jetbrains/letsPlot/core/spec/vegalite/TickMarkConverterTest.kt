/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.GeomName.fromGeomKind
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMap
import org.junit.Test
import java.util.Map.entry

class TickMarkConverterTest {
    @Test
    fun `with continuous x and discrete y tick should be vertical`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {"values": [{"a": 1, "b": 1}]},
                |  "mark": "tick",
                |  "encoding": {
                |    "x": { "field": "a", "type": "quantitative" },
                |    "y": { "field": "b", "type": "nominal" }
                |  }
                |}
            """.trimMargin()
        ).asMutable()

        val spec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(spec.getMap(Plot.LAYERS, 0)).containsOnly(
            entry(PlotBase.DATA, mapOf("a" to listOf(1.0), "b" to listOf(1.0))),
            entry(
                Meta.DATA_META, mapOf(
                    Meta.MappingAnnotation.TAG to listOf(
                        mapOf(
                            Meta.MappingAnnotation.AES to toOption(Aes.Y),
                            Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                            Meta.MappingAnnotation.PARAMETERS to mapOf(
                                Meta.MappingAnnotation.LABEL to "b",
                                Meta.MappingAnnotation.ORDER to 1
                            )
                        )
                    )
                )
            ),
            entry(Layer.GEOM, fromGeomKind(GeomKind.CROSS_BAR)),
            entry(Option.Geom.CrossBar.FATTEN, 0.0),
            entry(
                PlotBase.MAPPING, mapOf(
                    toOption(Aes.XMIN) to "a",
                    toOption(Aes.XMAX) to "a",
                    toOption(Aes.Y) to "b"
                )
            ),
        )
    }

    @Test
    fun `with both axis continuous tick should be vertical`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {"values": [{"a": 1, "b": 1}]},
                |  "mark": "tick",
                |  "encoding": {
                |    "x": { "field": "a", "type": "quantitative" },
                |    "y": { "field": "b", "type": "quantitative" }
                |  }
                |}
            """.trimMargin()
        ).asMutable()

        val spec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(spec.getMap(Plot.LAYERS, 0)).containsOnly(
            entry(PlotBase.DATA, mapOf("a" to listOf(1.0), "b" to listOf(1.0))),
            entry(Meta.DATA_META, empty()),
            entry(Layer.GEOM, fromGeomKind(GeomKind.CROSS_BAR)),
            entry(Option.Geom.CrossBar.FATTEN, 0.0),
            entry(
                PlotBase.MAPPING, mapOf(
                    toOption(Aes.XMIN) to "a",
                    toOption(Aes.XMAX) to "a",
                    toOption(Aes.Y) to "b"
                )
            ),
        )
    }

    @Test
    fun `with discrete x and continuous y tick should be horizontal`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {"values": [{"a": 1, "b": 1}]},
                |  "mark": "tick",
                |  "encoding": {
                |    "x": { "field": "a", "type": "nominal" },
                |    "y": { "field": "b", "type": "quantitative" }
                |  }
                |}
            """.trimMargin()
        ).asMutable()

        val spec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(spec.getMap(Plot.LAYERS, 0)).containsOnly(
            entry(PlotBase.DATA, mapOf("a" to listOf(1.0), "b" to listOf(1.0))),
            entry(
                Meta.DATA_META, mapOf(
                    Meta.MappingAnnotation.TAG to listOf(
                        mapOf(
                            Meta.MappingAnnotation.AES to toOption(Aes.X),
                            Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                            Meta.MappingAnnotation.PARAMETERS to mapOf(
                                Meta.MappingAnnotation.LABEL to "a",
                                Meta.MappingAnnotation.ORDER to 1
                            )
                        )
                    )
                )
            ),
            entry(Layer.GEOM, fromGeomKind(GeomKind.CROSS_BAR)),
            entry(Option.Geom.CrossBar.FATTEN, 0.0),
            entry(
                PlotBase.MAPPING, mapOf(
                    toOption(Aes.X) to "a",
                    toOption(Aes.YMIN) to "b",
                    toOption(Aes.YMAX) to "b"
                )
            ),
        )
    }

    @Test
    fun `with both axis discrete tick should be vertical`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {"values": [{"a": 1, "b": 1}]},
                |  "mark": "tick",
                |  "encoding": {
                |    "x": { "field": "a", "type": "nominal" },
                |    "y": { "field": "b", "type": "nominal" }
                |  }
                |}
            """.trimMargin()
        ).asMutable()

        val spec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(spec.getMap(Plot.LAYERS, 0)).containsOnly(
            entry(PlotBase.DATA, mapOf("a" to listOf(1.0), "b" to listOf(1.0))),
            entry(
                Meta.DATA_META, mapOf(
                    Meta.MappingAnnotation.TAG to listOf(
                        mapOf(
                            Meta.MappingAnnotation.AES to toOption(Aes.XMIN),
                            Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                            Meta.MappingAnnotation.PARAMETERS to mapOf(
                                Meta.MappingAnnotation.LABEL to "a",
                                Meta.MappingAnnotation.ORDER to 1
                            )
                        ),
                        mapOf(
                            Meta.MappingAnnotation.AES to toOption(Aes.XMAX),
                            Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                            Meta.MappingAnnotation.PARAMETERS to mapOf(
                                Meta.MappingAnnotation.LABEL to "a",
                                Meta.MappingAnnotation.ORDER to 1
                            )
                        ),
                        mapOf(
                            Meta.MappingAnnotation.AES to toOption(Aes.Y),
                            Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                            Meta.MappingAnnotation.PARAMETERS to mapOf(
                                Meta.MappingAnnotation.LABEL to "b",
                                Meta.MappingAnnotation.ORDER to 1
                            )
                        )
                    )
                )
            ),
            entry(Layer.GEOM, fromGeomKind(GeomKind.CROSS_BAR)),
            entry(Option.Geom.CrossBar.FATTEN, 0.0),
            entry(
                PlotBase.MAPPING, mapOf(
                    toOption(Aes.XMIN) to "a",
                    toOption(Aes.XMAX) to "a",
                    toOption(Aes.Y) to "b"
                )
            ),
        )
    }

}