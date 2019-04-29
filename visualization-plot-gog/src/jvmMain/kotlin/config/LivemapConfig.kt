package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.visualization.plot.gog.config.Option.Geom.Livemap
import jetbrains.datalore.visualization.plot.gog.config.Option.GeomName
import jetbrains.datalore.visualization.plot.gog.config.Option.Layer.GEOM
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.LAYERS
import jetbrains.datalore.visualization.plot.gog.core.render.geom.LivemapGeom
import jetbrains.datalore.visualization.plot.gog.core.render.geom.LivemapGeom.*
import java.util.stream.Collectors
import java.util.stream.Stream

class LivemapConfig private constructor(options: Map<*, *>) : OptionsAccessor(options, emptyMap<Any, Any>()) {

    fun createLivemapOptions(): LivemapOptions {
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

        if (has(Livemap.ZOOM)) {
            zoom = getInteger(Livemap.ZOOM)
        }

        if (has(Livemap.LOCATION)) {
            location = get(Livemap.LOCATION)
        }

        if (has(Livemap.STROKE)) {
            stroke = getDouble(Livemap.STROKE)
        }

        if (has(Livemap.INTERACTIVE)) {
            interactive = getBoolean(Livemap.INTERACTIVE)
        }

        if (has(Livemap.MAGNIFIER)) {
            magnifier = getBoolean(Livemap.MAGNIFIER)
        }

        if (has(Livemap.DISPLAY_MODE)) {
            displayMode = getDisplayMode(getString(Livemap.DISPLAY_MODE))
        }

        if (has(Livemap.FEATURE_LEVEL)) {
            featureLevel = getString(Livemap.FEATURE_LEVEL)
        }

        if (has(Livemap.PARENT)) {
            parent = get(Livemap.PARENT)
        }

        if (has(Livemap.SCALED)) {
            scaled = getBoolean(Livemap.SCALED)
        }

        if (has(Livemap.CLUSTERING)) {
            clustering = getBoolean(Livemap.CLUSTERING)
        }

        if (has(Livemap.LABELS)) {
            labels = getBoolean(Livemap.LABELS)
        }

        if (has(Livemap.THEME)) {
            theme = getTheme(getString(Livemap.THEME))
        }

        if (has(Livemap.PROJECTION)) {
            projection = getProjection(getString(Livemap.PROJECTION))
        }

        if (has(Livemap.GEODESIC)) {
            geodesic = getBoolean(Livemap.GEODESIC)
        }

        if (has(Livemap.DEV_PARAMS)) {
            devParams = getMap(Livemap.DEV_PARAMS)
        }

        return LivemapOptions(
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
            throw IllegalArgumentException(Livemap.THEME + validValues(Theme.values()))
        }

    }

    private fun getProjection(projection: String?): Projection {
        try {
            return Projection.valueOf(projection!!.toUpperCase())
        } catch (ignored: Exception) {
            throw IllegalArgumentException(Livemap.PROJECTION + validValues(Projection.values()))
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


        fun getLivemapOptions(plotOptions: Map<*, *>): Map<*, *>? {
            val plotOptionsAccessor = OptionsAccessor(plotOptions)
            if (!plotOptionsAccessor.has(LAYERS)) {
                return null
            }

            // ToDo: do not return null
            val layersList = plotOptionsAccessor.getList(LAYERS)
            if (layersList.isEmpty() || layersList[0] !is Map<*, *>) {
                return null
            }

            val layerOptions = layersList[0] as Map<*, *>
            return if (GeomName.LIVE_MAP == layerOptions[GEOM]) {
                layerOptions
            } else null
        }

        fun create(livemapOtherOptions: Map<*, *>): LivemapConfig {
            return LivemapConfig(livemapOtherOptions)
        }

        fun <ValueT : Enum<ValueT>> validValues(values: Array<ValueT>): String {
            val enumNames = Stream.of(*values)
                    .map<String> { it.name }
                    .map<String> { it.toLowerCase() }
                    .map { s -> "'$s'" }
                    .collect(Collectors.toList())
            return "=[" + enumNames.joinToString("|") + "]"
        }

        fun getDisplayMode(displayMode: String?): LivemapGeom.DisplayMode {
            try {
                return LivemapGeom.DisplayMode.valueOf(displayMode!!.toUpperCase())
            } catch (ignored: Exception) {
                throw IllegalArgumentException("geom" + validValues(LivemapGeom.DisplayMode.values()))
            }

        }
    }
}
