package jetbrains.datalore.visualization.plot.config

import jetbrains.datalore.visualization.plot.base.livemap.LivemapConstants.*
import jetbrains.datalore.visualization.plot.config.Option.Geom.LiveMap
import jetbrains.datalore.visualization.plot.config.Option.GeomName
import jetbrains.datalore.visualization.plot.config.Option.Layer.GEOM
import jetbrains.datalore.visualization.plot.config.Option.Plot.LAYERS

class LiveMapConfig private constructor(options: Map<*, *>) : OptionsAccessor(options, emptyMap<Any, Any>()) {

    fun createLivemapOptions(): LiveMapOptions {
        var zoom: Int? = null
        var location: Any? = null
        var stroke: Double? = null
        var interactive = DEF_INTERACTIVE
        var magnifier = DEF_MAGNIFIER
        var displayMode = DEF_DISPLAY_MODE
        var featureLevel: String? = null
        var parent: Any? = null
        var scaled = DEF_SCALED
        var clustering = DEF_CLUSTERING
        var labels = DEF_LABELS
        var theme = DEF_THEME
        var projection = DEF_PROJECTION
        var geodesic = DEF_GEODESIC
        var devParams: Map<*, *> = emptyMap<Any, Any>()

        if (has(LiveMap.ZOOM)) {
            zoom = getInteger(LiveMap.ZOOM)
        }

        if (has(LiveMap.LOCATION)) {
            location = get(LiveMap.LOCATION)
        }

        if (has(LiveMap.STROKE)) {
            stroke = getDouble(LiveMap.STROKE)
        }

        if (has(LiveMap.INTERACTIVE)) {
            interactive = getBoolean(LiveMap.INTERACTIVE)
        }

        if (has(LiveMap.MAGNIFIER)) {
            magnifier = getBoolean(LiveMap.MAGNIFIER)
        }

        if (has(LiveMap.DISPLAY_MODE)) {
            displayMode = getDisplayMode(getString(LiveMap.DISPLAY_MODE))
        }

        if (has(LiveMap.FEATURE_LEVEL)) {
            featureLevel = getString(LiveMap.FEATURE_LEVEL)
        }

        if (has(LiveMap.PARENT)) {
            parent = get(LiveMap.PARENT)
        }

        if (has(LiveMap.SCALED)) {
            scaled = getBoolean(LiveMap.SCALED)
        }

        if (has(LiveMap.CLUSTERING)) {
            clustering = getBoolean(LiveMap.CLUSTERING)
        }

        if (has(LiveMap.LABELS)) {
            labels = getBoolean(LiveMap.LABELS)
        }

        if (has(LiveMap.THEME)) {
            theme = getTheme(getString(LiveMap.THEME))
        }

        if (has(LiveMap.PROJECTION)) {
            projection = getProjection(getString(LiveMap.PROJECTION))
        }

        if (has(LiveMap.GEODESIC)) {
            geodesic = getBoolean(LiveMap.GEODESIC)
        }

        if (has(LiveMap.DEV_PARAMS)) {
            devParams = getMap(LiveMap.DEV_PARAMS)
        }

        return LiveMapOptions(
            zoom,
            location,
            stroke,
            interactive,
            magnifier,
            displayMode,
            featureLevel,
            parent,
            scaled,
            clustering,
            labels,
            theme,
            projection,
            geodesic,
            devParams
        )
    }

    private fun getTheme(theme: String?): Theme {
        try {
            return Theme.valueOf(theme!!.toUpperCase())
        } catch (ignored: Exception) {
            throw IllegalArgumentException(LiveMap.THEME + validValues(Theme.values()))
        }

    }

    private fun getProjection(projection: String?): Projection {
        try {
            return Projection.valueOf(projection!!.toUpperCase())
        } catch (ignored: Exception) {
            throw IllegalArgumentException(LiveMap.PROJECTION + validValues(Projection.values()))
        }

    }

    companion object {
        private val DEF_INTERACTIVE = true
        private val DEF_MAGNIFIER = false
        private val DEF_DISPLAY_MODE = DisplayMode.POLYGON
        private val DEF_SCALED = false
        private val DEF_CLUSTERING = false
        private val DEF_LABELS = true
        private val DEF_THEME = Theme.COLOR
        private val DEF_PROJECTION = Projection.EPSG3857
        private val DEF_GEODESIC = true


        fun getLiveMapOptions(plotOptions: Map<*, *>): Map<*, *> {
            val plotOptionsAccessor = OptionsAccessor(plotOptions)
            if (!plotOptionsAccessor.has(LAYERS)) {
                return emptyMap<Any, Any>()
            }

            val layersList = plotOptionsAccessor.getList(LAYERS)
            if (layersList.isEmpty() || layersList[0] !is Map<*, *>) {
                return emptyMap<Any, Any>()
            }

            val layerOptions = layersList[0] as Map<*, *>
            return if (GeomName.LIVE_MAP == layerOptions[GEOM]) {
                layerOptions
            } else emptyMap<Any, Any>()
        }

        fun create(livemapOtherOptions: Map<*, *>): LiveMapConfig {
            return LiveMapConfig(livemapOtherOptions)
        }

        fun <ValueT : Enum<ValueT>> validValues(values: Array<ValueT>): String {
            return values
                .map { it.name }
                .map { it.toLowerCase() }
                .joinToString("|", "=[", "]") { s -> "'$s'" }
        }

        fun getDisplayMode(displayMode: String?): DisplayMode {
            try {
                return DisplayMode.valueOf(displayMode!!.toUpperCase())
            } catch (ignored: Exception) {
                throw IllegalArgumentException("geom" + validValues(DisplayMode.values()))
            }
        }
    }
}
