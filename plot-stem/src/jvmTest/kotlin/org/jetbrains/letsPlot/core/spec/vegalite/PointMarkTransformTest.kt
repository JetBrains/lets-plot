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
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.junit.Test
import java.util.Map.entry

class PointMarkTransformTest {

    @Test
    fun `const size should be square rooted`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "mark": "point",
                |  
                |  "data": { "values": [ { "a": 1 } ]  },
                |  "encoding": { 
                |    "x": { "field": "a" },
                |     "size": { "value": 100 }
                |  }
                |}
        """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getDouble(Plot.LAYERS, 0, toOption(Aes.SIZE))).isEqualTo(10.0)
    }

    @Test
    fun typeAsString() {
        val vegaSpec = parseJson(
            """
                |{
                |  "mark": "point",
                |  
                |  "data": { "values": [ { "a": 1 } ]  },
                |  "encoding": { "x": { "field": "a" } }
                |}
        """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)
        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMaps(Plot.LAYERS)!!.first().typed<String, Any?>()).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(PlotBase.DATA, mapOf("a" to listOf(1.0))),
            entry(PlotBase.MAPPING, mapOf("x" to "a")),
            entry(Meta.DATA_META, mapOf(
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
            )),
        )
    }
}
