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
import org.jetbrains.letsPlot.core.spec.Option.Mapping.GROUP
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.BackendTestUtil
import org.jetbrains.letsPlot.core.spec.getMap
import java.util.Map.entry
import kotlin.test.Test

class DataTransformTest {

    @Test
    fun simple() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {"A": "C", "b": 2}, {"A": "C", "b": 7}, {"A": "C", "b": 4},
                |      {"A": "D", "b": 1}, {"A": "D", "b": 2}, {"A": "D", "b": 6},
                |      {"A": "E", "b": 8}, {"A": "E", "b": 4}, {"A": "E", "b": 7}
                |    ]
                |  },
                |  "mark": "point",
                |  "encoding": {
                |    "x": {"field": "A"},
                |    "y": {"field": "b"}
                |  }
                |}
        """.trimMargin()
        ).filterNotNullValues().asMutable()

        BackendTestUtil.backendSpecTransform(vegaSpec)
            .getMap(Plot.LAYERS, 0, PlotBase.DATA)
            .let {
                assertThat(it).containsOnly(
                    entry("A", listOf("C", "C", "C", "D", "D", "D", "E", "E", "E")),
                    entry("b", listOf(2.0, 7.0, 4.0, 1.0, 2.0, 6.0, 8.0, 4.0, 7.0))
                )
            }
    }


    @Test
    fun `plot(data=XXX, layer(data=null) - no data in layer`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [ {"A": "1"}, {"A": "2"}, {"A": "3"} ]
                |  },
                |  "layer": [
                |    {
                |      "data": null,
                |      "mark": "point"
                |    }
                |  ]
                |}
        """.trimMargin()
        ).filterNotNullValues().asMutable()

        val spec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(spec.getMap(PlotBase.DATA)).isNull()
        assertThat(spec.getMap(Plot.LAYERS, 0, PlotBase.DATA)).isEmpty()
    }

    @Test
    fun `plot(data=XXX, layer()) - layer data is XXX`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [ {"A": "1"}, {"A": "2"}, {"A": "3"} ]
                |  },
                |  "layer": [
                |    {
                |      "mark": "point",
                |      "encoding": { "x": {"field": "A"} }
                |    }
                |  ]
                |}
        """.trimMargin()
        ).filterNotNullValues().asMutable()

        val spec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(spec.getMap(PlotBase.DATA)).isNull()
        assertThat(spec.getMap(Plot.LAYERS, 0, PlotBase.DATA)).containsOnly(
            entry("A", listOf("1", "2", "3"))
        )
    }

    @Test
    fun `plot(data=XXX, layer(data=YYY) - layer data is YYY`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [ {"A": "1"}, {"A": "2"}, {"A": "3"} ]
                |  },
                |  "layer": [
                |    {
                |      "data": {
                |        "values": [ {"A": "4"}, {"A": "5"}, {"A": "6"} ]
                |      },
                |      "mark": "point",
                |      "encoding": { "x": {"field": "A"} }
                |    }
                |  ]
                |}
        """.trimMargin()
        ).filterNotNullValues().asMutable()

        val spec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(spec.getMap(PlotBase.DATA)).isNull()
        assertThat(spec.getMap(Plot.LAYERS, 0, PlotBase.DATA)).containsOnly(
            entry("A", listOf("4", "5", "6"))
        )
    }

    @Test
    fun `plot(mapping(x=A), layer(mapping(x=B)) - layer mapping(x=B)`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [ 
                |       {"A": 1, "B": 4}, 
                |       {"A": 2, "B": 5}, 
                |       {"A": 3, "B": 6} 
                |    ]
                |  },
                |  "encoding": { "x": {"field": "A"} },
                |  "layer": [
                |    {
                |      "mark": "point",
                |      "encoding": { "x": {"field": "B"} }
                |    }
                |  ]
                |}
        """.trimMargin()
        ).filterNotNullValues().asMutable()

        val spec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(spec.getMap(PlotBase.DATA)).isNull()
        assertThat(spec.getMap(Plot.LAYERS, 0)).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(PlotBase.DATA, mapOf("B" to listOf(4.0, 5.0, 6.0))),
            entry(PlotBase.MAPPING, mapOf("x" to "B")),
            entry(
                Meta.DATA_META, mapOf(
                    Meta.MappingAnnotation.TAG to listOf(
                        mapOf(
                            Meta.MappingAnnotation.AES to "x",
                            Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                            Meta.MappingAnnotation.PARAMETERS to mapOf(
                                Meta.MappingAnnotation.LABEL to "B",
                                Meta.MappingAnnotation.ORDER to 1
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `plot(mapping(x=A), layer(mapping(y=B)) - layer mapping(x=A, y=B)`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [ 
                |       {"A": 1, "B": 4}, 
                |       {"A": 2, "B": 5}, 
                |       {"A": 3, "B": 6} 
                |    ]
                |  },
                |  "encoding": { "x": {"field": "A"} },
                |  "layer": [
                |    {
                |      "mark": "point",
                |      "encoding": { "y": {"field": "B"} }
                |    }
                |  ]
                |}
        """.trimMargin()
        ).filterNotNullValues().asMutable()

        val spec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(spec.getMap(PlotBase.DATA)).isNull()
        assertThat(spec.getMap(Plot.LAYERS, 0)).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(
                PlotBase.DATA, mapOf(
                    "A" to listOf(1.0, 2.0, 3.0),
                    "B" to listOf(4.0, 5.0, 6.0)
                )
            ),
            entry(
                PlotBase.MAPPING, mapOf(
                    toOption(Aes.X) to "A",
                    toOption(Aes.Y) to "B"
                )
            ),
            entry(
                Meta.DATA_META, mapOf(
                    Meta.MappingAnnotation.TAG to listOf(
                        mapOf(
                            Meta.MappingAnnotation.AES to "x",
                            Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                            Meta.MappingAnnotation.PARAMETERS to mapOf(
                                Meta.MappingAnnotation.LABEL to "A",
                                Meta.MappingAnnotation.ORDER to 1
                            )
                        ),
                        mapOf(
                            Meta.MappingAnnotation.AES to "y",
                            Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                            Meta.MappingAnnotation.PARAMETERS to mapOf(
                                Meta.MappingAnnotation.LABEL to "B",
                                Meta.MappingAnnotation.ORDER to 1
                            )
                        ),
                    )
                )
            )
        )
    }


    @Test
    fun `plot(mapping=A, layer()) - layer mapping is A`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [ {"A": 1}, {"A": 2}, {"A": 3} ]
                |  },
                |  "encoding": { "x": {"field": "A"} },
                |  "layer": [
                |    {
                |      "mark": "point"
                |    }
                |  ]
                |}
        """.trimMargin()
        ).filterNotNullValues().asMutable()

        val spec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(spec.getMap(PlotBase.DATA)).isNull()
        assertThat(spec.getMap(Plot.LAYERS, 0)).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(PlotBase.DATA, mapOf("A" to listOf(1.0, 2.0, 3.0))),
            entry(PlotBase.MAPPING, mapOf("x" to "A")),
            entry(
                Meta.DATA_META, mapOf(
                    Meta.MappingAnnotation.TAG to listOf(
                        mapOf(
                            Meta.MappingAnnotation.AES to "x",
                            Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                            Meta.MappingAnnotation.PARAMETERS to mapOf(
                                Meta.MappingAnnotation.LABEL to "A",
                                Meta.MappingAnnotation.ORDER to 1
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `plot(mapping=(), layer(mapping=A)) - layer mapping is A`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [ {"A": 1}, {"A": 2}, {"A": 3} ]
                |  },
                |  "layer": [
                |    {
                |      "mark": "point",
                |      "encoding": { "x": {"field": "A"} }
                |    }
                |  ]
                |}
        """.trimMargin()
        ).filterNotNullValues().asMutable()

        val spec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(spec.getMap(PlotBase.DATA)).isNull()
        assertThat(spec.getMap(Plot.LAYERS, 0)).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(PlotBase.DATA, mapOf("A" to listOf(1.0, 2.0, 3.0))),
            entry(PlotBase.MAPPING, mapOf("x" to "A")),
            entry(
                Meta.DATA_META, mapOf(
                    Meta.MappingAnnotation.TAG to listOf(
                        mapOf(
                            Meta.MappingAnnotation.AES to "x",
                            Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                            Meta.MappingAnnotation.PARAMETERS to mapOf(
                                Meta.MappingAnnotation.LABEL to "A",
                                Meta.MappingAnnotation.ORDER to 1
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun groupingVarMapping() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {"A": 1, "B": "Foo"}, 
                |      {"A": 2, "B": "Bar"}
                |    ]
                |  },
                |  "mark": "point",
                |  "encoding": {
                |    "x": {"field": "A"},
                |    "detail": {"field": "B"}
                |  }
                |}
        """.trimMargin()
        ).filterNotNullValues().asMutable()

        val spec = BackendTestUtil.backendSpecTransform(vegaSpec)
        spec.getMap(Plot.LAYERS, 0, PlotBase.MAPPING).let {
            assertThat(it).containsOnly(
                entry(toOption(Aes.X), "A"),
                entry(GROUP, "B")
            )
        }
    }
}
