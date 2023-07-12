/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.spatial.projections.identity
import jetbrains.datalore.base.spatial.projections.mercator
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.GeomKind.*
import jetbrains.datalore.plot.builder.assemble.geom.DefaultSampling
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.builder.sampling.Sampling
import jetbrains.datalore.plot.builder.sampling.Samplings
import jetbrains.datalore.plot.config.Option.Geom
import jetbrains.datalore.plot.config.Option.Layer
import jetbrains.datalore.plot.config.Option.Meta
import jetbrains.datalore.plot.config.Option.Pos

class GeomProto(val geomKind: GeomKind) {

    fun geomProvider(layerConfig: OptionsAccessor): GeomProvider {
        return GeomProviderFactory.createGeomProvider(geomKind, layerConfig)
    }

    fun defaultOptions(): Map<String, Any> {
        require(DEFAULTS.containsKey(geomKind)) { "Default values doesn't support geom kind: '$geomKind'" }
        return DEFAULTS.getValue(geomKind)
    }

    fun preferredCoordinateSystem(layerConfig: OptionsAccessor): CoordProvider? {
        return when (geomKind) {
            TILE,
            BIN_2D,
            CONTOUR,
            CONTOURF,
            DENSITY2D,
            DENSITY2DF,
            RASTER,
            IMAGE -> CoordProviders.fixed(1.0)

            MAP -> CoordProviders.map(projection = identity().takeIf { layerConfig.has(Layer.USE_CRS) } ?: mercator())

            else -> null
        }
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
            PIE -> DefaultSampling.PIE
            LOLLIPOP -> DefaultSampling.LOLLIPOP
            LIVE_MAP,
            RASTER,
            IMAGE -> Samplings.NONE
        }
    }

    fun hasOwnPositionAdjustmentOptions(layerOptions: OptionsAccessor): Boolean {
        return when (geomKind) {
            JITTER -> layerOptions.hasOwn(Geom.Jitter.WIDTH) || layerOptions.hasOwn(Geom.Jitter.HEIGHT)
            TEXT, LABEL -> layerOptions.hasOwn(Geom.Text.NUDGE_X) || layerOptions.hasOwn(Geom.Text.NUDGE_Y)
            else -> false
        }
    }

    fun preferredPositionAdjustmentOptions(layerOptions: OptionsAccessor): Map<String, Any> {
        val posOptionValue: Any = when (geomKind) {
            JITTER -> mapOf(
                Meta.NAME to PosProto.JITTER,
                Pos.Jitter.WIDTH to layerOptions.getDouble(Geom.Jitter.WIDTH),
                Pos.Jitter.HEIGHT to layerOptions.getDouble(Geom.Jitter.HEIGHT),
            )

            Y_DOT_PLOT -> if (layerOptions.hasOwn(Geom.YDotplot.STACKGROUPS) &&
                layerOptions.getBoolean(Geom.YDotplot.STACKGROUPS)
            ) {
                PosProto.IDENTITY
            } else {
                mapOf(
                    Meta.NAME to PosProto.DODGE,
                    Pos.Dodge.WIDTH to 0.95
                )
            }

            TEXT, LABEL -> if (layerOptions.hasOwn(Geom.Text.NUDGE_X) || layerOptions.hasOwn(Geom.Text.NUDGE_Y)) {
                mapOf(
                    Meta.NAME to PosProto.NUDGE,
                    Pos.Nudge.WIDTH to layerOptions.getDouble(Geom.Text.NUDGE_X),
                    Pos.Nudge.HEIGHT to layerOptions.getDouble(Geom.Text.NUDGE_Y)
                )
            } else {
                PosProto.IDENTITY
            }

            else -> {
                // Some stats don't have their own geometries, but have own positioning
                when (StatKind.safeValueOf(layerOptions.getStringSafe(Layer.STAT))) {
                    StatKind.BOXPLOT_OUTLIER -> mapOf(
                        Meta.NAME to PosProto.DODGE,
                        Pos.Dodge.WIDTH to 0.95
                    )
                    // Some other geoms have stateless position adjustments defined in `defaults`
                    // Otherwise it's just `identity`
                    else -> DEFAULTS[geomKind]?.get(Layer.POS) ?: PosProto.IDENTITY
                }
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
            DEFAULTS[ERROR_BAR] = verticalIntervalDefaults()
            DEFAULTS[CROSS_BAR] = verticalIntervalDefaults()
            DEFAULTS[LINE_RANGE] = verticalIntervalDefaults()
            DEFAULTS[POINT_RANGE] = verticalIntervalDefaults()
            DEFAULTS[BOX_PLOT] = boxplotDefaults()
            DEFAULTS[AREA_RIDGES] = areaRidgesDefaults()
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
            DEFAULTS[PIE] = pieDefaults()
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
            defaults[Layer.POS] = PosProto.STACK
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


        private fun verticalIntervalDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "identity"
            defaults[Layer.POS] = mapOf(
                Meta.NAME to PosProto.DODGE,
                Pos.Dodge.WIDTH to 0.95
            )
            return defaults
        }

        private fun boxplotDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "boxplot"
            defaults[Layer.POS] = mapOf(
                Meta.NAME to PosProto.DODGE,
                Pos.Dodge.WIDTH to 0.95
            )
            return defaults
        }

        private fun areaRidgesDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "densityridges"
            return defaults
        }

        private fun violinDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults[Layer.STAT] = "ydensity"
            defaults[Layer.POS] = mapOf(
                Meta.NAME to PosProto.DODGE,
                Pos.Dodge.WIDTH to 0.95
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

        private fun pieDefaults(): Map<String, Any> {
            return mapOf(
                Layer.STAT to "count2d"
            )
        }
    }
}
