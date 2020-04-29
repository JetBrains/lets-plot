/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.livemap.LiveMapOptions
import jetbrains.datalore.plot.base.livemap.LivemapConstants.DisplayMode
import jetbrains.datalore.plot.base.livemap.LivemapConstants.Projection
import jetbrains.datalore.plot.config.Option.Geom.LiveMap

class LiveMapOptionsParser {
    companion object {

        fun parseFromLayerOptions(liveMapLayerOptions: OptionsAccessor): LiveMapOptions {
            return parseLivemapOptions(liveMapLayerOptions)
        }

        fun parseFromPlotOptions(plotOptions: OptionsAccessor): LiveMapOptions? {
            if (!plotOptions.has(Option.Plot.LAYERS)) {
                return null
            }

            val layersList = plotOptions.getList(Option.Plot.LAYERS)
            if (layersList.isEmpty() || layersList[0] !is Map<*, *>) {
                return null
            }

            val layerOptions = layersList[0] as Map<*, *>
            if (Option.GeomName.LIVE_MAP != layerOptions[Option.Layer.GEOM]) {
                return null
            }

            return parseLivemapOptions(OptionsAccessor(layerOptions))
        }

        private fun parseLivemapOptions(opts: OptionsAccessor): LiveMapOptions {

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
                throw IllegalArgumentException("geom" + formatValues(
                    DisplayMode.values()
                ))
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
