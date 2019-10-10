package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.config.Option.Geom.LiveMap
import jetbrains.datalore.visualization.plot.base.livemap.LivemapConstants.*

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
                opts.getInteger(LiveMap.ZOOM),
                opts.get(LiveMap.LOCATION),
                opts.getDouble(LiveMap.STROKE),
                opts.getBoolean(LiveMap.INTERACTIVE, true),
                opts.getBoolean(LiveMap.MAGNIFIER, false),
                opts.getString(LiveMap.DISPLAY_MODE)?.let(::parseDisplayMode) ?: DisplayMode.POLYGON,
                opts.getString(LiveMap.FEATURE_LEVEL),
                opts.get(LiveMap.PARENT),
                opts.getBoolean(LiveMap.SCALED, false),
                opts.getBoolean(LiveMap.CLUSTERING, false),
                opts.getBoolean(LiveMap.LABELS, true),
                opts.getString(LiveMap.THEME)?.let(::parseTheme) ?: Theme.COLOR,
                opts.getString(LiveMap.PROJECTION)?.let(::parseProjection) ?: Projection.EPSG3857,
                opts.getBoolean(LiveMap.GEODESIC, true),
                opts.getMap(LiveMap.DEV_PARAMS)
            )
        }

        private fun <ValueT : Enum<ValueT>> formatValues(values: Array<ValueT>): String {
            return values.joinToString("|", "=[", "]") { "'${it.name.toLowerCase()}'" }
        }

        private fun parseDisplayMode(displayMode: String): DisplayMode {
            try {
                return DisplayMode.valueOf(displayMode.toUpperCase())
            } catch (ignored: Exception) {
                throw IllegalArgumentException("geom" + formatValues(
                    DisplayMode.values()
                )
                )
            }
        }

        private fun parseTheme(theme: String): Theme {
            try {
                return Theme.valueOf(theme.toUpperCase())
            } catch (ignored: Exception) {
                throw IllegalArgumentException(
                    LiveMap.THEME + formatValues(
                        Theme.values()
                    )
                )
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
