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
import org.jetbrains.letsPlot.core.spec.Option.GeomName.fromStatKind
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.StatKind
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.typed
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

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.BOX_PLOT)),
            entry(PlotBase.DATA, mapOf(
                "..middle.." to listOf(0.5425600435859647),
                "..upper.." to listOf(0.7674347291529088),
                "..ymax.." to listOf(1.5792128155073915),
                "..lower.." to listOf(-0.46341769281246226),
                "..ymin.." to listOf(-0.4694743859349521),
                "S" to listOf(0.0)
            )),
            entry(Meta.DATA_META, mapOf(
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
            )),
            entry(PlotBase.MAPPING, mapOf("x" to "S", "y" to "V")),
        )

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![1].typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(PlotBase.DATA, mapOf(
                "..middle.." to listOf(0.5425600435859647),
                "..upper.." to listOf(0.7674347291529088),
                "..ymax.." to listOf(1.5792128155073915),
                "..lower.." to listOf(-0.46341769281246226),
                "..ymin.." to listOf(-0.4694743859349521),
                "V" to listOf(Double.NaN),
                "S" to listOf(0.0),
            )),
            entry(PlotBase.MAPPING, mapOf("x" to "S", "y" to "V")),
            entry(Meta.DATA_META, mapOf(
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
            )),
            entry(Layer.STAT, fromStatKind(StatKind.BOXPLOT_OUTLIER)),
        )
    }
}