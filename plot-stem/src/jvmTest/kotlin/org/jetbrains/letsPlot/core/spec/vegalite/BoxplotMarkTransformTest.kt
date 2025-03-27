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
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.back.BackendTestUtil
import java.util.Map.entry
import kotlin.test.Test

class BoxplotMarkTransformTest {
    @Test
    fun simple() {
        val vegaSpec = parseJson(
            """
            |{
            |  "data": { "values": [
            |    { "V": 1.5792128155073915, "S": 0 },
            |    { "V": 0.7674347291529088, "S": 0 },
            |    { "V": -0.4694743859349521, "S": 0 },
            |    { "V": 0.5425600435859647, "S": 0 },
            |    { "V": -0.46341769281246226, "S": 0 }
            |  ]},
            |  "mark": "boxplot",
            |  "encoding": { 
            |       "x": { "field": "S" },
            |       "y": { "field": "V", "type": "quantitative" } 
            |   }
            |}
        """.trimMargin()
        ).asMutable()

        val plotSpec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.BOX_PLOT)),
            entry(
                PlotBase.DATA, mapOf(
                    "..middle.." to listOf(0.5425600435859647),
                    "..upper.." to listOf(0.7674347291529088),
                    "..ymax.." to listOf(1.5792128155073915),
                    "..lower.." to listOf(-0.46341769281246226),
                    "..ymin.." to listOf(-0.4694743859349521),
                    "S" to listOf(0.0)
                )
            ),
            entry(
                Meta.DATA_META, mapOf(
                    Meta.MappingAnnotation.TAG to listOf(
                        mapOf(
                            Meta.MappingAnnotation.AES to toOption(Aes.X),
                            Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                            Meta.MappingAnnotation.PARAMETERS to mapOf(
                                Meta.MappingAnnotation.LABEL to "S",
                                Meta.MappingAnnotation.ORDER to 1
                            )
                        )
                    )
                )
            ),
            entry(PlotBase.MAPPING, mapOf("x" to "S", "y" to "V")),
        )

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![1].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(
                PlotBase.DATA, mapOf(
                    "..middle.." to listOf(0.5425600435859647),
                    "..upper.." to listOf(0.7674347291529088),
                    "..ymax.." to listOf(1.5792128155073915),
                    "..lower.." to listOf(-0.46341769281246226),
                    "..ymin.." to listOf(-0.4694743859349521),
                    "V" to listOf(Double.NaN),
                    "S" to listOf(0.0),
                )
            ),
            entry(PlotBase.MAPPING, mapOf("x" to "S", "y" to "V")),
            entry(
                Meta.DATA_META, mapOf(
                    Meta.MappingAnnotation.TAG to listOf(
                        mapOf(
                            Meta.MappingAnnotation.AES to toOption(Aes.X),
                            Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                            Meta.MappingAnnotation.PARAMETERS to mapOf(
                                Meta.MappingAnnotation.LABEL to "S",
                                Meta.MappingAnnotation.ORDER to 1
                            )
                        )
                    )
                )
            ),
            entry(Layer.STAT, fromStatKind(StatKind.BOXPLOT_OUTLIER)),
        )
    }


    @Test
    fun `layer orientation auto detection`() {
        val vegaSpec = parseJson(
            """
            |{
            |  "data": { "values": [
            |    { "V": 1.5792128155073915, "S": 0 },
            |    { "V": 0.7674347291529088, "S": 0 },
            |    { "V": -0.4694743859349521, "S": 0 },
            |    { "V": 0.5425600435859647, "S": 0 },
            |    { "V": -0.46341769281246226, "S": 0 }
            |  ]},
            |  "mark": "boxplot",
            |  "encoding": { 
            |       "x": { "field": "V", "type": "quantitative" },
            |       "y": { "field": "S" }
            |   }
            |}
        """.trimMargin()
        ).asMutable()

        val plotSpec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(plotSpec.getMap(Plot.LAYERS, 0)).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.BOX_PLOT)),
            entry(Layer.ORIENTATION, "y"),
            entry(
                PlotBase.DATA, mapOf(
                    "..middle.." to listOf(0.5425600435859647),
                    "..upper.." to listOf(0.7674347291529088),
                    "..ymax.." to listOf(1.5792128155073915),
                    "..lower.." to listOf(-0.46341769281246226),
                    "..ymin.." to listOf(-0.4694743859349521),
                    "S" to listOf(0.0)
                )
            ),
            entry(
                Meta.DATA_META, mapOf(
                    Meta.MappingAnnotation.TAG to listOf(
                        mapOf(
                            Meta.MappingAnnotation.AES to toOption(Aes.Y),
                            Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                            Meta.MappingAnnotation.PARAMETERS to mapOf(
                                Meta.MappingAnnotation.LABEL to "S",
                                Meta.MappingAnnotation.ORDER to 1
                            )
                        )
                    )
                )
            ),
            entry(PlotBase.MAPPING, mapOf("x" to "V", "y" to "S")),
        )

        assertThat(plotSpec.getMap(Plot.LAYERS, 1)).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(Layer.ORIENTATION, "y"),
            entry(
                PlotBase.DATA, mapOf(
                    "..middle.." to listOf(0.5425600435859647),
                    "..upper.." to listOf(0.7674347291529088),
                    "..ymax.." to listOf(1.5792128155073915),
                    "..lower.." to listOf(-0.46341769281246226),
                    "..ymin.." to listOf(-0.4694743859349521),
                    "V" to listOf(Double.NaN),
                    "S" to listOf(0.0),
                )
            ),
            entry(PlotBase.MAPPING, mapOf("x" to "V", "y" to "S")),
            entry(
                Meta.DATA_META, mapOf(
                    Meta.MappingAnnotation.TAG to listOf(
                        mapOf(
                            Meta.MappingAnnotation.AES to toOption(Aes.Y),
                            Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                            Meta.MappingAnnotation.PARAMETERS to mapOf(
                                Meta.MappingAnnotation.LABEL to "S",
                                Meta.MappingAnnotation.ORDER to 1
                            )
                        )
                    )
                )
            ),
            entry(Layer.STAT, fromStatKind(StatKind.BOXPLOT_OUTLIER)),
        )

    }

    @Test
    fun `timeUnit marks variable as discrete and adjusts orientation`() {
        val vegaSpec = parseJson(
            """
                |{
                |    "mark": {
                |        "type": "boxplot", 
                |        "tooltip": { "content": "encoding" }
                |    }, 
                |    "encoding": {
                |        "x": { "field": "temp", "type": "quantitative" }, 
                |        "y": { "field": "date", "type": "temporal", "timeUnit": "year" }
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

        val plotSpec = BackendTestUtil.backendSpecTransform(vegaSpec)

        assertThat(plotSpec.getMap(Plot.LAYERS, 0)).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.BOX_PLOT)),
            entry(PlotBase.MAPPING, mapOf("x" to "temp", "y" to "date")),
            entry(
                PlotBase.DATA, mapOf(
                    "..middle.." to listOf(39.75),
                    "..upper.." to listOf(42.1),
                    "..ymax.." to listOf(43.5),
                    "..lower.." to listOf(38.849999999999994),
                    "..ymin.." to listOf(38.6),
                    "date" to listOf(1262304000000.0)
                )
            ),
            entry(Layer.ORIENTATION, "y"),
            entry(
                Meta.DATA_META, mapOf(
                    Meta.MappingAnnotation.TAG to listOf(
                        mapOf(
                            Meta.MappingAnnotation.AES to toOption(Aes.Y),
                            Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                            Meta.MappingAnnotation.PARAMETERS to mapOf(
                                Meta.MappingAnnotation.LABEL to "date"
                            )
                        )
                    ),
                    Meta.SeriesAnnotation.TAG to listOf(
                        mapOf(
                            Meta.SeriesAnnotation.COLUMN to "date",
                            Meta.SeriesAnnotation.TYPE to Meta.SeriesAnnotation.Types.DATE_TIME,
                        )
                    )
                )
            )
        )
    }
}