/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.jetbrains.letsPlot.core.spec.Option.GeomName.fromGeomKind
import org.jetbrains.letsPlot.core.spec.Option.GeomName.fromStatKind
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.Option.Pos
import org.jetbrains.letsPlot.core.spec.PosProto
import org.jetbrains.letsPlot.core.spec.StatKind
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMap
import org.junit.Test
import java.util.Map.entry

class StatTransformTest {

    @Test
    fun `encoding color channel to aggregate should produce mappings to COLOR and FILL`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {"values": [ 
                |    {"v": 0}, {"v": 1}, {"v": 1}, {"v": 2}, {"v": 2}, {"v": 2}, {"v": 2}, {"v": 3}, {"v": 3}, {"v": 4} 
                |  ]},
                |  "mark": "bar",
                |  "encoding": {
                |    "x": {"bin": true, "field": "v", "type": "quantitative"},
                |    "y": {"aggregate": "count"},
                |    "color": {"aggregate":"count"}
                |  }
                |}
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMap(PlotBase.DATA)).isNull()
        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMap(Plot.LAYERS, 0)!! - PlotBase.DATA).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.BAR)),
            entry(Layer.STAT, StatKind.BIN.name.lowercase()),
            entry(Meta.DATA_META, empty()),
            entry(PlotBase.MAPPING, mapOf(
                toOption(Aes.X) to "v",
                toOption(Aes.Y) to Stats.COUNT.name,
                toOption(Aes.COLOR) to Stats.COUNT.name,
                toOption(Aes.FILL) to Stats.COUNT.name,
            )),
        )
    }

    @Test
    fun `encoding color channel to transform should produce mappings to COLOR and FILL`() {
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
                |  "mark": "circle",
                |  "encoding": {
                |    "x": {
                |      "field": "value",
                |      "type": "quantitative"
                |    },
                |    "y": {
                |      "field": "density",
                |      "type": "quantitative"
                |    },
                |    "color": {
                |      "field": "density",
                |      "type": "quantitative"
                |    }
                |  }
                |}                
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMap(PlotBase.DATA)).isNull()
        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMap(Plot.LAYERS, 0)!! - PlotBase.DATA).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(Layer.STAT, fromStatKind(StatKind.DENSITY)),
            entry(Layer.POS, mapOf(Pos.NAME to PosProto.STACK)),
            entry(Meta.DATA_META, empty()),
            entry(toOption(Aes.SHAPE), 16),
            entry(PlotBase.MAPPING, mapOf(
                toOption(Aes.X) to "v",
                toOption(Aes.Y) to Stats.DENSITY.name,
                toOption(Aes.COLOR) to Stats.DENSITY.name,
                toOption(Aes.FILL) to Stats.DENSITY.name,
            )),
        )
    }

    @Test
    fun `if transform=density and stack is not set then position should be stack`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {"value": 21, "group": "A"}, {"value": 38, "group": "A"},
                |      {"value": 7, "group": "B"}, {"value": 8, "group": "B"},
                |      {"value": 13, "group": "B"}, {"value": 18, "group": "B"},
                |      {"value": 51, "group": "C"}, {"value": 51, "group": "C"},
                |      {"value": 55, "group": "C"}, {"value": 89, "group": "C"}
                |    ]
                |  },
                |  "transform": [
                |    {
                |      "density": "value",
                |      "groupby": ["group"]
                |    }
                |  ],
                |  "mark": "circle",
                |  "encoding": {
                |    "x": {
                |      "field": "value",
                |      "type": "quantitative"
                |    },
                |    "y": {
                |      "field": "density",
                |      "type": "quantitative"
                |    },
                |    "color": {
                |      "field": "group",
                |      "type": "nominal"
                |    }
                |  }
                |}
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMap(PlotBase.DATA)).isNull()
        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMap(Plot.LAYERS, 0)!! - PlotBase.DATA).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(Layer.STAT, fromStatKind(StatKind.DENSITY)),
            entry(Layer.POS, mapOf(Pos.NAME to PosProto.STACK)),
            entry(Meta.DATA_META, empty()),
            entry(toOption(Aes.SHAPE), 16),
            entry(PlotBase.MAPPING, mapOf(
                toOption(Aes.X) to "value",
                toOption(Aes.Y) to Stats.DENSITY.name,
                toOption(Aes.COLOR) to "group",
                toOption(Aes.FILL) to "group",
            )),
        )
    }

    @Test
    fun `if transform=density and stack=zero then position should be stack`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {"value": 21, "group": "A"}, {"value": 38, "group": "A"},
                |      {"value": 7, "group": "B"}, {"value": 8, "group": "B"},
                |      {"value": 13, "group": "B"}, {"value": 18, "group": "B"},
                |      {"value": 51, "group": "C"}, {"value": 51, "group": "C"},
                |      {"value": 55, "group": "C"}, {"value": 89, "group": "C"}
                |    ]
                |  },
                |  "transform": [
                |    {
                |      "density": "value",
                |      "groupby": ["group"]
                |    }
                |  ],
                |  "mark": "circle",
                |  "encoding": {
                |    "x": {
                |      "field": "value",
                |      "type": "quantitative"
                |    },
                |    "y": {
                |      "field": "density",
                |      "type": "quantitative",
                |      "stack": "zero"
                |    },
                |    "color": {
                |      "field": "group",
                |      "type": "nominal"
                |    }
                |  }
                |}
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMap(PlotBase.DATA)).isNull()
        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMap(Plot.LAYERS, 0)!! - PlotBase.DATA).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(Layer.STAT, fromStatKind(StatKind.DENSITY)),
            entry(Layer.POS, mapOf(Pos.NAME to PosProto.STACK)),
            entry(Meta.DATA_META, empty()),
            entry(toOption(Aes.SHAPE), 16),
            entry(PlotBase.MAPPING, mapOf(
                toOption(Aes.X) to "value",
                toOption(Aes.Y) to Stats.DENSITY.name,
                toOption(Aes.COLOR) to "group",
                toOption(Aes.FILL) to "group",
            )),
        )
    }

    @Test
    fun `if transform=density and stack=null then position should be identity`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {"value": 21, "group": "A"}, {"value": 38, "group": "A"},
                |      {"value": 7, "group": "B"}, {"value": 8, "group": "B"},
                |      {"value": 13, "group": "B"}, {"value": 18, "group": "B"},
                |      {"value": 51, "group": "C"}, {"value": 51, "group": "C"},
                |      {"value": 55, "group": "C"}, {"value": 89, "group": "C"}
                |    ]
                |  },
                |  "transform": [
                |    {
                |      "density": "value",
                |      "groupby": ["group"]
                |    }
                |  ],
                |  "mark": "circle",
                |  "encoding": {
                |    "x": {
                |      "field": "value",
                |      "type": "quantitative"
                |    },
                |    "y": {
                |      "field": "density",
                |      "type": "quantitative",
                |      "stack": null
                |    },
                |    "color": {
                |      "field": "group",
                |      "type": "nominal"
                |    }
                |  }
                |}
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMap(PlotBase.DATA)).isNull()
        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMap(Plot.LAYERS, 0)!! - PlotBase.DATA).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(Layer.STAT, fromStatKind(StatKind.DENSITY)),
            entry(Layer.POS, mapOf(Pos.NAME to PosProto.IDENTITY)),
            entry(Meta.DATA_META, empty()),
            entry(toOption(Aes.SHAPE), 16),
            entry(PlotBase.MAPPING, mapOf(
                toOption(Aes.X) to "value",
                toOption(Aes.Y) to Stats.DENSITY.name,
                toOption(Aes.COLOR) to "group",
                toOption(Aes.FILL) to "group",
            )),
        )
    }

}