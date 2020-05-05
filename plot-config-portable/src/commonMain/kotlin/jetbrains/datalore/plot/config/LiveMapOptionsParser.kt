/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.livemap.LiveMapOptions
import jetbrains.datalore.plot.base.livemap.LivemapConstants.DisplayMode
import jetbrains.datalore.plot.base.livemap.LivemapConstants.Projection
import jetbrains.datalore.plot.config.Option.Geom.LiveMap
import jetbrains.datalore.plot.config.Option.Plot.LAYERS

class LiveMapOptionsParser {
    companion object {

        fun parseFromPlotSpec(plotSpec: Map<String, Any>): LiveMapOptions? {
            fun Map<*, *>.isLiveMap(): Boolean = this[Option.Layer.GEOM] == Option.GeomName.LIVE_MAP

            val layers = plotSpec.getMaps(LAYERS)!!
            if (layers.any { it.isLiveMap() }) {
                require(layers.count { it.isLiveMap() } == 1) { "Only one geom_livemap is allowed per plot" }
                require(layers.first().isLiveMap()) { "geom_livemap should be a first geom" }
                return parseFromLayerOptions(OptionsAccessor(layers.first()))
            }

            return null
        }

        fun parseFromLayerOptions(opts: OptionsAccessor): LiveMapOptions {

            return LiveMapOptions(
                zoom = opts.getInteger(LiveMap.ZOOM),
                location = opts.get(LiveMap.LOCATION),
                stroke = opts.getDouble(LiveMap.STROKE),
                interactive = opts.getBoolean(LiveMap.INTERACTIVE, true),
                magnifier = opts.getBoolean(LiveMap.MAGNIFIER, false),
                displayMode = opts.getString(LiveMap.DISPLAY_MODE)?.let(::parseDisplayMode) ?: DisplayMode.POLYGON,
                featureLevel = opts.getString(LiveMap.FEATURE_LEVEL),
                parent = opts.get(LiveMap.PARENT),
                scaled = opts.getBoolean(LiveMap.SCALED, false),
                clustering = opts.getBoolean(LiveMap.CLUSTERING, false),
                labels = opts.getBoolean(LiveMap.LABELS, true),
                projection = opts.getString(LiveMap.PROJECTION)?.let(::parseProjection) ?: Projection.EPSG3857,
                geodesic = opts.getBoolean(LiveMap.GEODESIC, true),
                geocodingService = opts.getMap(LiveMap.GEOCODING),
                tileProvider = opts.getMap(LiveMap.TILES),
                devParams = opts.getMap(LiveMap.DEV_PARAMS)
            )
        }

        private fun <ValueT : Enum<ValueT>> formatValues(values: Array<ValueT>): String {
            return values.joinToString(prefix = "=[", separator = "|", postfix = "]") { "'${it.name.toLowerCase()}'" }
        }

        private fun parseDisplayMode(displayMode: String): DisplayMode {
            try {
                return DisplayMode.valueOf(displayMode.toUpperCase())
            } catch (ignored: Exception) {
                throw IllegalArgumentException("geom" + formatValues(DisplayMode.values()))
            }
        }

        private fun parseProjection(projection: String): Projection {
            try {
                return Projection.valueOf(projection.toUpperCase())
            } catch (ignored: Exception) {
                throw IllegalArgumentException(LiveMap.PROJECTION + formatValues(Projection.values()))
            }
        }
    }
}
