/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.GeomName.fromGeomKind
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.typed
import org.junit.Test
import java.util.Map.entry

class TextMarkTransformTest {
    @Test
    fun simple() {
        val vegaSpec = JsonSupport.parseJson(
            """
                |{
                |  "data": { "values": [{"b": 28}, {"b": 55}, {"b": 43}] },
                |  "mark": {"type": "text"},
                |  "encoding": {
                |       "x": {"field": "b", "type": "quantitative"}, 
                |       "text": {"field": "b"}
                |  }
                |}
            """.trimMargin()).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMaps(Plot.LAYERS)!![0].typed<String, Any?>()).containsOnly(
            entry(Option.Layer.GEOM, fromGeomKind(GeomKind.TEXT)),
            entry(
                Meta.DATA_META, mapOf(
                Meta.MappingAnnotation.TAG to listOf(
                    mapOf(
                        Meta.MappingAnnotation.AES to toOption(Aes.LABEL),
                        Meta.MappingAnnotation.ANNOTATION to Meta.MappingAnnotation.AS_DISCRETE,
                        Meta.MappingAnnotation.PARAMETERS to mapOf(
                            Meta.MappingAnnotation.LABEL to "b",
                            Meta.MappingAnnotation.ORDER to 1
                        )
                    ),
                )
            )),
            entry(Option.PlotBase.DATA, mapOf("b" to listOf(28.0, 55.0, 43.0))),
            entry(Option.PlotBase.MAPPING, mapOf(
                toOption(Aes.X) to "b",
                toOption(Aes.LABEL) to "b")
            )
        )
    }
}