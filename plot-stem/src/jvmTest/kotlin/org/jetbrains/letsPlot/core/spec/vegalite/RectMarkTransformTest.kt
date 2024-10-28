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
                |  "mark": { "type": "rect" },
                |  "encoding": {
                |    "x": { "field": "x_start", "type": "quantitative" },
                |    "x2": { "field": "x_end" },
                |    "y": { "field": "y_start", "type": "quantitative" },
                |    "y2": { "field": "y_end" }
                |  }
                |}
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.RECT)),
            entry(Meta.DATA_META, empty()),
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

    @Test
    fun heatmap() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {"Cylinders": 8, "Origin": "USA", "mean_Horsepower": 158.45},
                |      {"Cylinders": 4, "Origin": "Europe", "mean_Horsepower": 78.91},
                |      {"Cylinders": 4, "Origin": "Japan", "mean_Horsepower": 75.58},
                |      {"Cylinders": 6, "Origin": "USA", "mean_Horsepower": 99.67},
                |      {"Cylinders": 4, "Origin": "USA", "mean_Horsepower": 80.96},
                |      {"Cylinders": 3, "Origin": "Japan", "mean_Horsepower": 99.25},
                |      {"Cylinders": 6, "Origin": "Japan", "mean_Horsepower": 115.83},
                |      {"Cylinders": 6, "Origin": "Europe", "mean_Horsepower": 113.50},
                |      {"Cylinders": 5, "Origin": "Europe", "mean_Horsepower": 82.33}
                |    ]
                |  },
                |  "mark": "rect",
                |  "encoding": {
                |    "y": {"field": "Origin", "type": "nominal"},
                |    "x": {"field": "Cylinders", "type": "ordinal"},
                |    "color": {"field": "mean_Horsepower", "type": "quantitative"},
                |    "tooltip": {"type": "quantitative"}
                |  },
                |  "config": {"axis": {"grid": true, "tickBand": "extent"}}
                |}             
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.RASTER)),
            entry(
                PlotBase.DATA, mapOf(
                    "Origin" to listOf("USA", "Europe", "Japan", "USA", "USA", "Japan", "Japan", "Europe", "Europe"),
                    "mean_Horsepower" to listOf(158.45, 78.91, 75.58, 99.67, 80.96, 99.25, 115.83, 113.5, 82.33),
                    "Cylinders" to listOf(8.0, 4.0, 4.0, 6.0, 4.0, 3.0, 6.0, 6.0, 5.0)
                )
            ),
            entry(
                PlotBase.MAPPING, mapOf(
                    toOption(Aes.Y) to "Origin",
                    toOption(Aes.X) to "Cylinders",
                    toOption(Aes.FILL) to "mean_Horsepower"
                )
            ),
            entry(Meta.DATA_META, mapOf(
                Meta.MappingAnnotation.TAG to listOf(
                    mapOf(
                        Meta.MappingAnnotation.AES to toOption(Aes.X),
                        Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                        Meta.MappingAnnotation.PARAMETERS to mapOf(
                            Meta.MappingAnnotation.LABEL to "Cylinders",
                            Meta.MappingAnnotation.ORDER to 1
                        )
                    ),
                )
            ))
        )
    }
}