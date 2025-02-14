/*
 * Copyright (c) 2025. JetBrains s.r.o.
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
import org.jetbrains.letsPlot.core.spec.getMap
import org.junit.Test
import java.util.Map.entry

class GenericTests {
    @Test
    fun `color from mark property should be used if encoding channel is not present`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {"values": [{"name": "foo"}]},
                |  "layer": [
                |    {
                |      "mark": {"type": "point", "color": "blue"},
                |      "encoding": {}
                |    }
                |  ]
                |}
            """.trimMargin()
        ).asMutable()

        val plotSpec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMap(Plot.LAYERS, 0)).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(toOption(Aes.COLOR), "blue"),
            entry(PlotBase.DATA, empty()),
            entry(Meta.DATA_META, empty()),
            entry(PlotBase.MAPPING, empty()),
        )
    }

    @Test
    fun `constant channel value doesn't override the mapping`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {"values": [{"name": "foo"}]},
                |  "layer": [
                |    {
                |      "mark": {"type": "point", "color": "blue"},
                |      "encoding": {"color": {"value": "red"}}
                |    }
                |  ]
                |}
            """.trimMargin()
        ).asMutable()

        val plotSpec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMap(Plot.LAYERS, 0)).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(toOption(Aes.COLOR), "red"),
            entry(PlotBase.DATA, empty()),
            entry(Meta.DATA_META, empty()),
            entry(PlotBase.MAPPING, empty()),
        )
    }

    @Test
    fun another() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {"values": [{"a": "foo"}]},
                |  "layer": [
                |    {
                |      "mark": {"type": "point", "color": "green"},
                |      "encoding": {"color": {"field": "a"}}
                |    }
                |  ]
                |}
        """.trimMargin()
        ).asMutable()

        val plotSpec = BackendTestUtil.backendSpecTransform(vegaSpec)
        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMap(Plot.LAYERS, 0)).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(PlotBase.DATA, mapOf("a" to listOf("foo"))),
            entry(PlotBase.MAPPING, mapOf(toOption(Aes.COLOR) to "a")),
            entry(Meta.DATA_META, empty()),
        )
    }
}