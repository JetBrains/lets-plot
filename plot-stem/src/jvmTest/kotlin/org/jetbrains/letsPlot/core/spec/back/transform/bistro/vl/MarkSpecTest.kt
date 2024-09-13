/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.vl

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.getMaps
import org.junit.Test
import kotlin.collections.get

class MarkSpecTest {
    @Test
    fun typeAsString() {
        val vegaSpec = parseJson("""
                |{
                |  "mark": "point",
                |  
                |  "data": { "values": [ { "a": 1 } ]  },
                |  "encoding": { "x": { "field": "a" } }
                |}
        """.trimMargin()).asMutable()

        SpecTransformBackendUtil.processTransform(vegaSpec).let { spec ->
            spec.getMaps(Plot.LAYERS)!!.first().let {
                assertThat(it[Layer.GEOM]).isEqualTo(GeomKind.POINT.name.lowercase())
            }
        }
    }


    @Test
    fun typeInMarkObject() {
        val vegaSpec = parseJson("""
                |{
                |  "mark": { "type": "bar" },
                |  
                |  "data": { "values": [ { "a": 1 } ]  },
                |  "encoding": { "x": { "field": "a" } }
                |}
        """.trimMargin()).asMutable()

        SpecTransformBackendUtil.processTransform(vegaSpec).let { spec ->
            spec.getMaps(Plot.LAYERS)!!.first().let {
                assertThat(it[Layer.GEOM]).isEqualTo(GeomKind.BAR.name.lowercase())
            }
        }
    }

    @Test
    fun propertiesInMarkObject() {
        val vegaSpec = parseJson(
            """
                |{
                |  "mark": { "type": "bar", "width": 0.5 },
                |  
                |  "data": { "values": [ { "a": 1 } ]  },
                |  "encoding": { "x": { "field": "a" } }
                |}
        """.trimMargin()
        ).asMutable()

        SpecTransformBackendUtil.processTransform(vegaSpec).let { spec ->
            spec.getMaps(Plot.LAYERS)!!.first().let {
                assertThat(it[Layer.GEOM]).isEqualTo(GeomKind.BAR.name.lowercase())
            }
        }
    }


    @Test
    fun propertiesInMarkObjectInLayer() {
        val vegaSpec = parseJson(
            """
                |{
                |  "layer": [ {"mark": { "type": "bar", "width": 0.5 } }],
                |  
                |  "data": { "values": [ { "a": 1 } ]  },
                |  "encoding": { "x": { "field": "a" } }
                |}
        """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)
        plotSpec.getMaps(Plot.LAYERS)!!.first().let {
            assertThat(it[Layer.GEOM]).isEqualTo(GeomKind.BAR.name.lowercase())
        }
        assertThat(plotSpec.getMap(Option.PlotBase.DATA))
            .isEqualTo(mapOf("a" to listOf(1.0)))

        assertThat(plotSpec.getMap(Option.PlotBase.MAPPING))
            .isEqualTo(mapOf("x" to "a"))

    }


}