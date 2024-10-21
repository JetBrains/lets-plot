/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMap
import org.junit.Test
import java.util.Map.entry

class CoordinateSystemTransformTest {

    @Test
    fun xyDomain() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {"wavelength": 250, "power": 1},
                |      {"wavelength": 300, "power": 2},
                |      {"wavelength": 420, "power": 4},
                |      {"wavelength": 450, "power": 1.8},
                |      {"wavelength": 500, "power": 1.1}
                |    ]
                |  },
                |  "layer": [
                |    {
                |      "mark": {"type": "line", "clip": true, "strokeWidth": 10 },
                |      "encoding": {
                |        "x": {
                |          "field": "wavelength",
                |          "type": "quantitative",
                |          "scale": {"domain": [300, 450]}
                |        },
                |        "y": {
                |          "field": "power", 
                |          "type": "quantitative",
                |          "scale": {"domain": [1.5, 3]}
                |        }
                |      }
                |    }
                |  ]
                |}
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMap(Option.Plot.COORD)).containsOnly(
            entry(Option.Meta.NAME, Option.CoordName.CARTESIAN),
            entry(Option.Coord.X_LIM, listOf(300.0, 450.0)),
            entry(Option.Coord.Y_LIM, listOf(1.5, 3.0))
        )
    }

    @Test
    fun xyMinMaxDomain() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {
                |    "values": [
                |      {"wavelength": 250, "power": 1},
                |      {"wavelength": 300, "power": 2},
                |      {"wavelength": 420, "power": 4},
                |      {"wavelength": 450, "power": 1.8},
                |      {"wavelength": 500, "power": 1.1}
                |    ]
                |  },
                |  "layer": [
                |    {
                |      "mark": {"type": "line", "clip": true, "strokeWidth": 10 },
                |      "encoding": {
                |        "x": {
                |          "field": "wavelength",
                |          "type": "quantitative",
                |          "scale": {"domainMin": 300}
                |        },
                |        "y": {
                |          "field": "power", 
                |          "type": "quantitative",
                |          "scale": {"domainMax": 3}
                |        }
                |      }
                |    }
                |  ]
                |}
            """.trimMargin()
        ).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMap(Option.Plot.COORD)).containsOnly(
            entry(Option.Meta.NAME, Option.CoordName.CARTESIAN),
            entry(Option.Coord.X_LIM, listOf(300.0, null)),
            entry(Option.Coord.Y_LIM, listOf(null, 3.0))
        )
    }

}