/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.CoordName
import org.jetbrains.letsPlot.core.spec.Option.GeomName.fromGeomKind
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.asMutable
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.getMap
import org.junit.Ignore
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

    @Ignore
    @Test
    fun `lonlat channel should produce xy mapping with coord_map`() {
        val vegaSpec = parseJson(
            """
                |{
                |  "data": {"values": [
                |    {"lon": 170, "lat": 0, "z": "foo"}, 
                |    {"lon": 170, "lat": 20, "z": "bar"}, 
                |    {"lon": 170, "lat": 40, "z": "baz"},
                |    {"lon": 170, "lat": 60, "z": "spam"},
                |    {"lon": 170, "lat": 80, "z": "foobar"}
                |  ]},
                |  "mark": "point",
                |  "encoding": {
                |    "longitude": {"field": "lon"},
                |    "latitude": {"field": "lat"},
                |    "color": {"field": "z"}
                |  }
                |}
            """.trimMargin()).asMutable()

        val plotSpec = SpecTransformBackendUtil.processTransform(vegaSpec)

        assertThat(plotSpec.getMap(PlotBase.DATA)).isNull()
        assertThat(plotSpec.getMap(PlotBase.MAPPING)).isNull()
        assertThat(plotSpec.getMap(Plot.COORD)).containsOnly(entry(Option.Meta.NAME, CoordName.MAP))
        assertThat(plotSpec.getMap(Plot.LAYERS, 0)).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.LIVE_MAP)),
            entry(PlotBase.DATA, empty()),
            entry(Option.Geom.LiveMap.TILES, mapOf(
                Option.Geom.LiveMap.Tile.KIND to Option.Geom.LiveMap.Tile.KIND_VECTOR_LETS_PLOT,
                Option.Geom.LiveMap.Tile.THEME to Option.Geom.LiveMap.Tile.THEME_COLOR,
                Option.Geom.LiveMap.Tile.URL to "wss://tiles.datalore.jetbrains.com",
                Option.Geom.LiveMap.Tile.ATTRIBUTION to "<a href=\"https://lets-plot.org\">© Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">© OpenStreetMap contributors</a>.",
            )
            )
        )
        assertThat(plotSpec.getMap(Plot.LAYERS, 1)).containsOnly(
            entry(Layer.GEOM, fromGeomKind(GeomKind.POINT)),
            entry(
                PlotBase.DATA, mapOf<String, List<Any?>>(
                    "lon" to listOf(170.0, 170.0, 170.0, 170.0, 170.0),
                    "lat" to listOf(0.0, 20.0, 40.0, 60.0, 80.0),
                    "z" to listOf("foo", "bar", "baz", "spam", "foobar")
                )
            ),
            entry(PlotBase.MAPPING, mapOf(
                toOption(Aes.X) to "lon",
                toOption(Aes.Y) to "lat",
                toOption(Aes.COLOR) to "z"
            )),
            entry(Meta.DATA_META, empty()),
        )
    }

}