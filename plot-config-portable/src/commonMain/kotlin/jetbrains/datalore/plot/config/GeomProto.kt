/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.GeomKind.*
import jetbrains.datalore.plot.base.GeomMeta
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.assemble.geom.DefaultSampling
import jetbrains.datalore.plot.builder.sampling.Sampling
import jetbrains.datalore.plot.builder.sampling.Samplings
import jetbrains.datalore.plot.config.Option.Meta

open class GeomProto constructor(val geomKind: GeomKind) {

    fun defaultOptions(): Map<String, Any> {
        require(DEFAULTS.containsKey(geomKind)) { "Default values doesn't support geom kind: '$geomKind'" }
        return DEFAULTS[geomKind]!!
    }

    fun preferredSampling(): Sampling {
        return when (geomKind) {
            POINT -> DefaultSampling.POINT
            PATH -> DefaultSampling.PATH
            LINE -> DefaultSampling.LINE
            SMOOTH -> DefaultSampling.SMOOTH
            BAR -> DefaultSampling.BAR
            HISTOGRAM -> DefaultSampling.HISTOGRAM
            DOT_PLOT -> DefaultSampling.DOT_PLOT
            TILE -> DefaultSampling.TILE
            BIN_2D -> DefaultSampling.BIN_2D
            ERROR_BAR -> DefaultSampling.ERROR_BAR
            CROSS_BAR -> DefaultSampling.CROSS_BAR
            LINE_RANGE -> DefaultSampling.LINE_RANGE
            POINT_RANGE -> DefaultSampling.POINT_RANGE
            CONTOUR -> DefaultSampling.CONTOUR
            CONTOURF -> DefaultSampling.CONTOURF
            POLYGON -> DefaultSampling.POLYGON
            MAP -> DefaultSampling.MAP
            AB_LINE -> DefaultSampling.AB_LINE
            H_LINE -> DefaultSampling.H_LINE
            V_LINE -> DefaultSampling.V_LINE
            BOX_PLOT -> Samplings.NONE // DefaultSampling.BOX_PLOT
            VIOLIN -> DefaultSampling.VIOLIN
            Y_DOT_PLOT -> DefaultSampling.Y_DOT_PLOT
            RIBBON -> DefaultSampling.RIBBON
            AREA -> DefaultSampling.AREA
            DENSITY -> DefaultSampling.DENSITY
            DENSITY2D -> DefaultSampling.DENSITY2D
            DENSITY2DF -> DefaultSampling.DENSITY2DF
            JITTER -> DefaultSampling.JITTER
            FREQPOLY -> DefaultSampling.FREQPOLY
            STEP -> DefaultSampling.STEP
            RECT -> DefaultSampling.RECT
            SEGMENT -> DefaultSampling.SEGMENT
            TEXT -> DefaultSampling.TEXT

            LIVE_MAP,
            RASTER,
            IMAGE -> Samplings.NONE
        }
    }

    fun renders(): List<Aes<*>> {
        return GeomMeta.renders(geomKind)
    }

    fun preferredPositionAdjustments(layerOptions: OptionsAccessor): PosProvider {
        return when (geomKind) {
            JITTER -> PosProvider.jitter(
                layerOptions.getDouble(Option.Geom.Jitter.WIDTH),
                layerOptions.getDouble(Option.Geom.Jitter.HEIGHT)
            )
            Y_DOT_PLOT -> if (layerOptions.hasOwn(Option.Geom.YDotplot.STACKGROUPS) &&
                              layerOptions.getBoolean(Option.Geom.YDotplot.STACKGROUPS)) {
                PosProvider.wrap(PositionAdjustments.identity())
            } else {
                PosProvider.dodge(0.95)
            }

            // Some other geoms has stateless position adjustments defined in `defaults`
            // Otherwise it's just `identity`
            else -> PosProvider.wrap(PositionAdjustments.identity())
        }
    }

    private companion object {
        private val DEFAULTS = HashMap<GeomKind, Map<String, Any>>()
        private val COMMON = commonDefaults()

        init {
            for (geomKind in values()) {
                DEFAULTS[geomKind] = COMMON
            }

            DEFAULTS[SMOOTH] =
                smoothDefaults()
            DEFAULTS[BAR] =
                barDefaults()
            DEFAULTS[HISTOGRAM] =
                histogramDefaults()
            DEFAULTS[DOT_PLOT] =
                dotplotDefaults()
            DEFAULTS[CONTOUR] =
                contourDefaults()
            DEFAULTS[CONTOURF] =
                contourfDefaults()
            DEFAULTS[CROSS_BAR] =
                crossBarDefaults()
            DEFAULTS[BOX_PLOT] =
                boxplotDefaults()
            DEFAULTS[VIOLIN] =
                violinDefaults()
            DEFAULTS[Y_DOT_PLOT] =
                yDotplotDefaults()
            DEFAULTS[AREA] =
                areaDefaults()
            DEFAULTS[DENSITY] =
                densityDefaults()
            DEFAULTS[DENSITY2D] =
                density2dDefaults()
            DEFAULTS[DENSITY2DF] =
                density2dfDefaults()
            DEFAULTS[FREQPOLY] =
                freqpolyDefaults()
            DEFAULTS[BIN_2D] =
                bin2dDefaults()
        }

        private fun commonDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "identity"
            return defaults
        }

        private fun smoothDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "smooth"
            return defaults
        }

        private fun barDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "count"
            defaults["position"] = "stack"
            return defaults
        }

        private fun histogramDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "bin"
            defaults["position"] = "stack"
            return defaults
        }

        private fun dotplotDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "dotplot"
            defaults["position"] = "identity"
            return defaults
        }

        private fun contourDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "contour"
            return defaults
        }

        private fun contourfDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "contourf"
            return defaults
        }


        private fun crossBarDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "identity"
            defaults["position"] = mapOf(Meta.NAME to "dodge", "width" to 0.95)
            return defaults
        }

        private fun boxplotDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "boxplot"
            defaults["position"] = mapOf(Meta.NAME to "dodge", "width" to 0.95)
            return defaults
        }

        private fun violinDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "ydensity"
            defaults["position"] = mapOf(Meta.NAME to "dodge", "width" to 0.95)
            return defaults
        }

        private fun yDotplotDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "ydotplot"
            return defaults
        }

        private fun areaDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "identity"
            defaults["position"] = "stack"
            return defaults
        }

        private fun densityDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "density"
            return defaults
        }

        private fun density2dDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "density2d"
            return defaults
        }

        private fun density2dfDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "density2df"
            return defaults
        }

        private fun freqpolyDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "bin"
            return defaults
        }

        private fun bin2dDefaults(): Map<String, Any> {
            return mapOf(
                "stat" to "bin2d"
            )
        }
    }
}
