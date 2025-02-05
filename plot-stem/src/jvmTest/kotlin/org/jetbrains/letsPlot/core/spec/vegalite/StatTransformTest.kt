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
import org.jetbrains.letsPlot.core.spec.Option.Stat
import org.jetbrains.letsPlot.core.spec.PosProto
import org.jetbrains.letsPlot.core.spec.StatKind
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMap
import org.junit.Test
import java.util.Map.entry

class StatTransformTest {

    @Test
    fun `timeUnit - basic case`() {
        val vegaSpec = parseJson(
            """
                |{
                |    "mark": {
                |        "type": "boxplot", 
                |        "tooltip": { "content": "encoding" }
                |    }, 
                |    "encoding": {
                |        "x": { "timeUnit": "year", "field": "date", "type": "temporal" }, 
                |        "y": { "field": "temp", "type": "quantitative" }
                |    }, 
                |    "params": [
                |        { "name": "p0", "select": "interval", "bind": "scales" }
                |    ], 
                |    "data": {
                |        "values": [
                |            { "date": 1262304000000, "temp": 39.4 }, 
                |            { "date": 1262307600000, "temp": 39.2 }, 
                |            { "date": 1262311200000, "temp": 39 }, 
                |            { "date": 1262314800000, "temp": 38.9 }, 
                |            { "date": 1262318400000, "temp": 38.8 }, 
                |            { "date": 1262322000000, "temp": 38.7 }, 
                |            { "date": 1262325600000, "temp": 38.7 }, 
                |            { "date": 1262329200000, "temp": 38.6 }, 
                |            { "date": 1262332800000, "temp": 38.7 }, 
                |            { "date": 1262336400000, "temp": 39.2 }, 
                |            { "date": 1262340000000, "temp": 40.1 }, 
                |            { "date": 1262343600000, "temp": 41.3 }, 
                |            { "date": 1262347200000, "temp": 42.5 }, 
                |            { "date": 1262350800000, "temp": 43.2 }, 
                |            { "date": 1262354400000, "temp": 43.5 }, 
                |            { "date": 1262358000000, "temp": 43.3 }, 
                |            { "date": 1262361600000, "temp": 42.7 }, 
                |            { "date": 1262365200000, "temp": 41.7 }, 
                |            { "date": 1262368800000, "temp": 41.2 }, 
                |            { "date": 1262372400000, "temp": 40.9 }
                |        ]
                |    }
                |}                
            """.trimMargin()
        ).asMutable()

        val spec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(spec.getMap(Plot.LAYERS, 0, PlotBase.DATA)).containsOnly(
            entry("..lower..", listOf(38.849999999999994)),
            entry("..middle..", listOf(39.75)),
            entry("..upper..", listOf(42.1)),
            entry("..ymax..", listOf(43.5)),
            entry("..ymin..", listOf(38.6)),
            entry("date", listOf(1.262304E12)),
        )
    }

    @Test
    fun `count ignores field`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "mark": {
                |    "type": "bar",
                |    "tooltip": { "content": "encoding" }
                |  },
                |  "encoding": {
                |    "x": { "field": "race", "type": "nominal" },
                |    "y": { "aggregate": "count", "field": "race", "type": "quantitative" },
                |    "color": { "field": "type", "type": "nominal" }
                |  },
                |  "params": [
                |    { "name": "p0", "select": "interval", "bind": "scales" }
                |  ],
                |  "data": {
                |    "values": [
                |      { "race": "Latino", "type": "Officer-involved shooting" }, 
                |      { "race": "Latino", "type": "Not riot-related" }, 
                |      { "race": "Latino", "type": "Homicide" }, 
                |      { "race": "Black", "type": "Officer-involved shooting" }, 
                |      { "race": "Black", "type": "Death" }, 
                |      { "race": "Latino", "type": "Officer-involved shooting" }, 
                |      { "race": "Black", "type": "Death" }, 
                |      { "race": "White", "type": "Homicide" }, 
                |      { "race": "Latino", "type": "Homicide" }
                |    ]
                |  }
                |}
                
            """.trimMargin()
        ).asMutable()

        val spec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(spec.getMap(Plot.LAYERS, 0, PlotBase.DATA)).containsOnly(
            entry("race", listOf("Latino", "Black", "Latino", "Latino", "White", "Black")),
            entry("type", listOf("Officer-involved shooting", "Officer-involved shooting", "Not riot-related", "Homicide", "Homicide", "Death")),
            entry("..count..", listOf(2.0, 1.0, 1.0, 2.0, 1.0, 2.0)),
        )
    }

    @Test
    fun `bin source axis without type should be continuous`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {"values": [
                |    {"v": 0}, {"v": 1}, {"v": 1}, {"v": 2}, {"v": 2}, {"v": 2}, {"v": 2}, {"v": 3}, {"v": 3}, {"v": 4}
                |  ]},
                |  "mark": "bar",
                |  "encoding": {
                |    "x": {"bin": {"step":1}, "field": "v"},
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
            entry(Layer.GEOM, fromGeomKind(GeomKind.HISTOGRAM)),
            entry(Layer.STAT, StatKind.BIN.name.lowercase()),
            entry(Stat.Bin.BINWIDTH, 1.0),
            entry(Meta.DATA_META, empty()), // All axis are continuous
            entry(PlotBase.MAPPING, mapOf(
                toOption(Aes.X) to "v",
                toOption(Aes.Y) to Stats.COUNT.name,
                toOption(Aes.COLOR) to Stats.COUNT.name,
                toOption(Aes.FILL) to Stats.COUNT.name,
            )),
        )

    }

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
                |    "x": {"bin": {"step":1}, "field": "v", "type": "quantitative"},
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
            entry(Layer.GEOM, fromGeomKind(GeomKind.HISTOGRAM)),
            entry(Layer.STAT, StatKind.BIN.name.lowercase()),
            entry(Stat.Bin.BINWIDTH, 1.0),
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


    @Test
    fun `if bin stat output bound to x then layer orientation should be y`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {"values": [
                |    {"v": 0}, {"v": 1}, {"v": 1}, {"v": 2}, {"v": 2}, {"v": 2}, {"v": 2}, {"v": 3}, {"v": 3}, {"v": 4}
                |  ]},
                |  "mark": "bar",
                |  "encoding": {
                |    "x": {"aggregate": "count"},
                |    "y": {"bin": {"step":1}, "field": "v", "type": "quantitative"},
                |    "color": {"aggregate":"count"}
                |  }
                |}
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMap(PlotBase.DATA)).isNull()
        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMap(Plot.LAYERS, 0)!! - PlotBase.DATA).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.HISTOGRAM)),
            entry(Layer.ORIENTATION, "y"),
            entry(Layer.STAT, StatKind.BIN.name.lowercase()),
            entry(Stat.Bin.BINWIDTH, 1.0),
            entry(Meta.DATA_META, empty()),
            entry(PlotBase.MAPPING, mapOf(
                toOption(Aes.X) to Stats.COUNT.name,
                toOption(Aes.Y) to "v",
                toOption(Aes.COLOR) to Stats.COUNT.name,
                toOption(Aes.FILL) to Stats.COUNT.name,
            )),
        )
    }

    @Test
    fun `if density stat output bound to x then layer orientation should be y`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {"v": 1}, {"v": 1},
                |      {"v": 2}, {"v": 2}, {"v": 2},
                |      {"v": 3}, {"v": 3}, {"v": 3}, {"v": 3},
                |      {"v": 4}, {"v": 4}, {"v": 4}
                |    ]
                |  },
                |  "transform":[{
                |    "density": "v",
                |    "bandwidth": 0.3
                |  }],
                |  "mark": "circle",
                |  "encoding": {
                |    "x": { "field": "density", "type": "quantitative" },
                |    "y": { "field": "value", "type": "quantitative" },
                |    "color": { "field": "density", "type": "quantitative" }
                |  }
                |}                
            """.trimMargin()).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMap(PlotBase.DATA)).isNull()
        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMap(Plot.LAYERS, 0)!! - PlotBase.DATA).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(toOption(Aes.SHAPE), 16),
            entry(Layer.STAT, fromStatKind(StatKind.DENSITY)),
            entry(Layer.POS, mapOf(Pos.NAME to PosProto.STACK)),
            entry(Layer.ORIENTATION, "y"),
            entry(Layer.STAT, StatKind.DENSITY.name.lowercase()),
            entry(Meta.DATA_META, empty()),
            entry(PlotBase.MAPPING, mapOf(
                toOption(Aes.X) to Stats.DENSITY.name,
                toOption(Aes.Y) to "v",
                toOption(Aes.COLOR) to Stats.DENSITY.name,
                toOption(Aes.FILL) to Stats.DENSITY.name,
            )),
        )
    }

    @Test
    fun `if aggregate sum outpu bound to x then layer orientation should be y`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {"values": [
                |    {"v": 0, "g": 2}, 
                |    {"v": 1, "g": 2}, {"v": 1, "g": 2}, 
                |    {"v": 2, "g": 2}, {"v": 2, "g": 2}, {"v": 2, "g": 2}, {"v": 2, "g": 2}, 
                |    {"v": 3, "g": 2}, {"v": 3, "g": 2}, 
                |    {"v": 4, "g": 2}
                |  ]},
                |  "mark": "bar",
                |  "encoding": {
                |    "y": {"field": "v"},
                |    "x": {"field": "g", "aggregate": "sum"}
                |  }
                |}
            """.trimMargin()).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMap(PlotBase.DATA)).isNull()
        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMap(Plot.LAYERS, 0)).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.BAR)),
            entry(Layer.ORIENTATION, "y"),
            entry(Meta.DATA_META, mapOf(
                Meta.MappingAnnotation.TAG to listOf(
                    mapOf(
                        Meta.MappingAnnotation.AES to toOption(Aes.Y),
                        Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                        Meta.MappingAnnotation.PARAMETERS to mapOf(
                            Meta.MappingAnnotation.LABEL to "v",
                            Meta.MappingAnnotation.ORDER to 1
                        )
                    )
                )
            )),
            entry(
                PlotBase.DATA, mapOf<String, List<Any?>>(
                    "..ymax.." to listOf(2.0, 2.0, 2.0, 2.0, 2.0),
                    "..ymin.." to listOf(2.0, 2.0, 2.0, 2.0, 2.0),
                    "v" to listOf(0.0, 1.0, 2.0, 3.0, 4.0),
                    "g" to listOf(2.0, 4.0, 8.0, 4.0, 2.0),
                )
            ),
            entry(PlotBase.MAPPING, mapOf(
                toOption(Aes.X) to "g",
                toOption(Aes.Y) to "v",
            )),
            entry(Layer.STAT, StatKind.SUMMARY.name.lowercase()),
            entry(Stat.Summary.FUN, Stat.Summary.Functions.SUM)
        )

    }

}