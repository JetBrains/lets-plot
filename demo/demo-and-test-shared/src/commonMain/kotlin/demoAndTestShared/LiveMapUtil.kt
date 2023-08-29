/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demoAndTestShared

import org.jetbrains.letsPlot.core.spec.*

object LiveMapUtil {
    object Tiles {
        val production = mapOf(
                "kind" to "vector_lets_plot",
                "url" to "wss://tiles.datalore.jetbrains.com/",
                "theme" to "color",
                "attribution" to "Map: <a href=\"https://github.com/JetBrains/lets-plot\">\u00a9 Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">\u00a9 OpenStreetMap contributors</a>."
        )

        val productionDark = mapOf(
                "kind" to "vector_lets_plot",
                "url" to "wss://tiles.datalore.jetbrains.com/",
                "theme" to "dark",
                "attribution" to "Map: <a href=\"https://github.com/JetBrains/lets-plot\">\u00a9 Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">\u00a9 OpenStreetMap contributors</a>."
        )

        val dev = mapOf(
            "tiles" to mapOf(
                "kind" to "vector_lets_plot",
                "url" to "ws://10.0.0.127:3943",
                "min_zoom" to 1,
                "max_zoom" to 15,
                "theme" to "color"
            )
        )

        val nasa = mapOf(
            "tiles" to mapOf(
                "kind" to "raster_zxy",
                "url" to "https://gibs.earthdata.nasa.gov/wmts/epsg3857/best/ASTER_GDEM_Greyscale_Shaded_Relief/default//GoogleMapsCompatible_Level12/{z}/{y}/{x}.jpg",
                "attribution" to "<a href=\"https://earthdata.nasa.gov/eosdis/science-system-description/eosdis-components/gibs\">\u00a9 NASA Global Imagery Browse Services (GIBS)</a>",
                "min_zoom" to 1,
                "max_zoom" to 12
            )
        )

        val osm = mapOf(
            "tiles" to mapOf(
                "kind" to "raster_zxy",
                "url" to "https://[abc].tile.openstreetmap.org/{z}/{x}/{y}.png",
                "attribution" to "<a href=\"https://www.openstreetmap.org/copyright\">© OpenStreetMap contributors</a>"
            )
        )

        val debug = mapOf(
            "tiles" to mapOf(
                "kind" to "chessboard"
            )
        )
    }

    fun MutableMap<String, Any>.updateTiles(tilesSpec: Map<String, Any> = Tiles.production, force: Boolean = false) = apply {
        fun update(plotSpec: Map<*, *>) {
            try {
                val liveMapSpec = plotSpec.getList("layers")!!.asMaps().first()
                if (force || Option.Geom.LiveMap.TILES !in liveMapSpec) {
                    liveMapSpec.asMutable()[Option.Geom.LiveMap.TILES] = tilesSpec
                }

            } catch (e: Exception) {
                println(e)
            }
        }

        when(val specKind = get(Option.Meta.KIND)) {
            Option.Meta.Kind.PLOT -> update(this)
            Option.Meta.Kind.SUBPLOTS -> getMaps(Option.SubPlots.FIGURES)!!.forEach(::update)
            Option.Meta.Kind.GG_BUNCH -> getMaps(Option.GGBunch.ITEMS)!!.map { it.getMap(Option.GGBunch.Item.FEATURE_SPEC)!! }.forEach(::update)
            else -> error("Unknown spec kind: $specKind")
        }
    }
}