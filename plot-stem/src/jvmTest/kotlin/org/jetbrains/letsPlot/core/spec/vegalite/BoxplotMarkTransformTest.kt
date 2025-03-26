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
        val dataset = listOf(
            0 to 1.5792128155073915,
            0 to 0.7674347291529088,
            0 to -0.4694743859349521,
            0 to 0.5425600435859647,
            0 to -0.46341769281246226
        )
        val vegaSpec = parseJson(
            """
            |{
            |  "data": { "values": [${dataset.map { "{ \"S\": ${it.first}, \"V\": ${it.second} }" }.joinToString(", ")}] },
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
                    "S" to listOf(0.0),
                    "V" to listOf(dataset.map(Pair<Int, Double>::second).sum() / dataset.size) // Var is preserved in the PlotConfigBackend::variablesToKeep() because for boxplot Aes.Y now in the renderedAes list, according to GeomMeta
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
        val dataset = listOf(
            0 to 1.5792128155073915,
            0 to 0.7674347291529088,
            0 to -0.4694743859349521,
            0 to 0.5425600435859647,
            0 to -0.46341769281246226
        )
        val vegaSpec = parseJson(
            """
            |{
            |  "data": { "values": [${dataset.map { "{ \"S\": ${it.first}, \"V\": ${it.second} }" }.joinToString(", ")}] },
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
                    "S" to listOf(0.0),
                    "V" to listOf(dataset.map(Pair<Int, Double>::second).sum() / dataset.size) // Var is preserved in the PlotConfigBackend::variablesToKeep() because for boxplot Aes.Y now in the renderedAes list, according to GeomMeta
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
        val dataset = listOf(
            1262304000000 to 39.4,
            1262307600000 to 39.2,
            1262311200000 to 39.0,
            1262314800000 to 38.9,
            1262318400000 to 38.8,
            1262322000000 to 38.7,
            1262325600000 to 38.7,
            1262329200000 to 38.6,
            1262332800000 to 38.7,
            1262336400000 to 39.2,
            1262340000000 to 40.1,
            1262343600000 to 41.3,
            1262347200000 to 42.5,
            1262350800000 to 43.2,
            1262354400000 to 43.5,
            1262358000000 to 43.3,
            1262361600000 to 42.7,
            1262365200000 to 41.7,
            1262368800000 to 41.2,
            1262372400000 to 40.9
        )
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
                |    "data": { "values": [${dataset.map { "{ \"date\": ${it.first}, \"temp\": ${it.second} }" }.joinToString(", ")}] }
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
                    "date" to listOf(1262304000000.0),
                    "temp" to listOf(dataset.map(Pair<Long, Double>::second).sum() / dataset.size) // Var is preserved in the PlotConfigBackend::variablesToKeep() because for boxplot Aes.Y now in the renderedAes list, according to GeomMeta
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