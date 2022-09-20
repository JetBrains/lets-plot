/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.GeomKind.*
import jetbrains.datalore.plot.base.GeomMeta
import jetbrains.datalore.plot.builder.assemble.geom.DefaultSampling
import jetbrains.datalore.plot.builder.sampling.Sampling
import jetbrains.datalore.plot.builder.sampling.Samplings
import jetbrains.datalore.plot.config.Option.Geom
import jetbrains.datalore.plot.config.Option.Layer
import jetbrains.datalore.plot.config.Option.Meta

open class GeomProto constructor(val geomKind: GeomKind) {

    fun defaultOptions(): Map<String, Any> {
        require(DEFAULTS.containsKey(geomKind)) { "Default values doesn't support geom kind: '$geomKind'" }
        return DEFAULTS.getValue(geomKind)
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
            AREA_RIDGES -> DefaultSampling.AREA_RIDGES
            VIOLIN -> DefaultSampling.VIOLIN
            Y_DOT_PLOT -> DefaultSampling.Y_DOT_PLOT
            RIBBON -> DefaultSampling.RIBBON
            AREA -> DefaultSampling.AREA
            DENSITY -> DefaultSampling.DENSITY
            DENSITY2D -> DefaultSampling.DENSITY2D
            DENSITY2DF -> DefaultSampling.DENSITY2DF
            JITTER -> DefaultSampling.JITTER
            Q_Q -> DefaultSampling.Q_Q
            Q_Q_2 -> DefaultSampling.Q_Q
            Q_Q_LINE -> DefaultSampling.Q_Q_LINE
            Q_Q_2_LINE -> DefaultSampling.Q_Q_LINE
            FREQPOLY -> DefaultSampling.FREQPOLY
            STEP -> DefaultSampling.STEP
            RECT -> DefaultSampling.RECT
            SEGMENT -> DefaultSampling.SEGMENT
            TEXT, LABEL -> DefaultSampling.TEXT
            LIVE_MAP,
            RASTER,
            IMAGE -> Samplings.NONE
        }
    }

    fun renders(): List<Aes<*>> {
        return GeomMeta.renders(geomKind)
    }

    fun preferredPositionAdjustmentOptions(layerOptions: OptionsAccessor): Map<String, Any> {
        val posOptionValue: Any = when (geomKind) {
            JITTER -> mapOf(
                Meta.NAME to PosProto.JITTER,
                PosProto.JITTER_WIDTH to layerOptions.getDouble(Geom.Jitter.WIDTH),
                PosProto.JITTER_HEIGHT to layerOptions.getDouble(Geom.Jitter.HEIGHT),
            )
            Y_DOT_PLOT -> if (layerOptions.hasOwn(Geom.YDotplot.STACKGROUPS) &&
                layerOptions.getBoolean(Geom.YDotplot.STACKGROUPS)
            ) {
                PosProto.IDENTITY
            } else {
                mapOf(
                    Meta.NAME to PosProto.DODGE,
                    PosProto.DODGE_WIDTH to 0.95
                )
            }
            else -> {
                // Some other geoms has stateless position adjustments defined in `defaults`
                // Otherwise it's just `identity`
                DEFAULTS[geomKind]?.get(Layer.POS) ?: PosProto.IDENTITY
            }
        }

        return if (posOptionValue is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            (posOptionValue.filterValues { it != null } as Map<String, Any>)
        } else {
            mapOf(
                Meta.NAME to posOptionValue as String
            )
        }
    }

    private companion object {
        private val DEFAULTS = HashMap<GeomKind, Map<String, Any>>()
        private val COMMON = commonDefaults()

        init {
            for (geomKind in values()) {
                DEFAULTS[geomKind] = COMMON
            }

            DEFAULTS[SMOOTH] = smoothDefaults()
            DEFAULTS[BAR] = barDefaults()
            DEFAULTS[HISTOGRAM] = histogramDefaults()
            DEFAULTS[DOT_PLOT] = dotplotDefaults()
            DEFAULTS[CONTOUR] = contourDefaults()
            DEFAULTS[CONTOURF] = contourfDefaults()
            DEFAULTS[CROSS_BAR] = crossBarDefaults()
            DEFAULTS[BOX_PLOT] = boxplotDefaults()
            DEFAULTS[VIOLIN] = violinDefaults()
            DEFAULTS[Y_DOT_PLOT] = yDotplotDefaults()
            DEFAULTS[AREA] = areaDefaults()
            DEFAULTS[DENSITY] = densityDefaults()
            DEFAULTS[DENSITY2D] = density2dDefaults()
            DEFAULTS[DENSITY2DF] = density2dfDefaults()
            DEFAULTS[Q_Q] = qqDefaults()
            DEFAULTS[Q_Q_2] = qq2Defaults()
            DEFAULTS[Q_Q_LINE] = qqLineDefaults()
            DEFAULTS[Q_Q_2_LINE] = qq2LineDefaults()
            DEFAULTS[FREQPOLY] = freqpolyDefaults()
            DEFAULTS[BIN_2D] = bin2dDefaults()
        }

        private fun commonDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "identity"
            return defaults
        }

        private fun smoothDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "smooth"
            return defaults
        }

        private fun barDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "count"
            defaults[Layer.POS] = "stack"
            return defaults
        }

        private fun histogramDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "bin"
            defaults[Layer.POS] = PosProto.STACK
            return defaults
        }

        private fun dotplotDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "dotplot"
            defaults[Layer.POS] = "identity"
            return defaults
        }

        private fun contourDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "contour"
            return defaults
        }

        private fun contourfDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "contourf"
            return defaults
        }


        private fun crossBarDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "identity"
            defaults[Layer.POS] = mapOf(
                Meta.NAME to PosProto.DODGE,
                PosProto.DODGE_WIDTH to 0.95
            )
            return defaults
        }

        private fun boxplotDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "boxplot"
            defaults[Layer.POS] = mapOf(
                Meta.NAME to PosProto.DODGE,
                PosProto.DODGE_WIDTH to 0.95
            )
            return defaults
        }

        private fun violinDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "ydensity"
            defaults[Layer.POS] = mapOf(
                Meta.NAME to PosProto.DODGE,
                PosProto.DODGE_WIDTH to 0.95
            )
            return defaults
        }

        private fun yDotplotDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "ydotplot"
            return defaults
        }

        private fun areaDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "identity"
            defaults[Layer.POS] = PosProto.STACK
            return defaults
        }

        private fun densityDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "density"
            return defaults
        }

        private fun density2dDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "density2d"
            return defaults
        }

        private fun density2dfDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "density2df"
            return defaults
        }

        private fun qqDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "qq"
            return defaults
        }

        private fun qq2Defaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "qq2"
            return defaults
        }

        private fun qqLineDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "qq_line"
            return defaults
        }

        private fun qq2LineDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "qq2_line"
            return defaults
        }

        private fun freqpolyDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "bin"
            return defaults
        }

        private fun bin2dDefaults(): Map<String, Any> {
            return mapOf(
                Layer.STAT to "bin2d"
            )
        }
    }
}
