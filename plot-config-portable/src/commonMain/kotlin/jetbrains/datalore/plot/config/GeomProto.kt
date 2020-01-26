/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.GeomKind.*
import jetbrains.datalore.plot.base.GeomMeta
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.assemble.geom.DefaultSampling
import jetbrains.datalore.plot.builder.sampling.Sampling
import jetbrains.datalore.plot.builder.sampling.Samplings

open class GeomProto constructor(val geomKind: GeomKind) {

    fun defaultOptions(): Map<String, Any> {
        checkArgument(DEFAULTS.containsKey(geomKind), "Default values doesn't support geom kind: '$geomKind'")
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
            TILE -> DefaultSampling.TILE
            ERROR_BAR -> DefaultSampling.ERROR_BAR
            CROSS_BAR -> DefaultSampling.CROSS_BAR
            LINE_RANGE -> DefaultSampling.LINE_RANGE
            CONTOUR -> DefaultSampling.CONTOUR
            CONTOURF -> DefaultSampling.CONTOURF
            POLYGON -> DefaultSampling.POLYGON
            MAP -> DefaultSampling.MAP
            AB_LINE -> DefaultSampling.AB_LINE
            H_LINE -> DefaultSampling.H_LINE
            V_LINE -> DefaultSampling.V_LINE
            BOX_PLOT -> DefaultSampling.BOX_PLOT
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

            // Some other geoms has stateless position adjustments defined in `defaults`
            // Otherwise it's just `identity`
            else -> PosProvider.wrap(PositionAdjustments.identity())
        }
    }

    private companion object {
        private val DEFAULTS = HashMap<GeomKind, Map<String, Any>>()
        private val COMMON = createCommonDefaults()

        init {
            for (geomKind in values()) {
                DEFAULTS[geomKind] = COMMON
            }

            DEFAULTS[SMOOTH] =
                createSmoothDefaults()
            DEFAULTS[BAR] =
                createBarDefaults()
            DEFAULTS[HISTOGRAM] =
                createHistogramDefaults()
            DEFAULTS[CONTOUR] =
                createContourDefaults()
            DEFAULTS[CONTOURF] =
                createContourfDefaults()
            DEFAULTS[CROSS_BAR] =
                createCrossBarDefaults()
            DEFAULTS[BOX_PLOT] =
                createBoxplotDefaults()
            DEFAULTS[AREA] =
                createAreaDefaults()
            DEFAULTS[DENSITY] =
                createDensityDefaults()
            DEFAULTS[DENSITY2D] =
                createDensity2dDefaults()
            DEFAULTS[DENSITY2DF] =
                createDensity2dfDefaults()
            DEFAULTS[FREQPOLY] =
                createFreqpolyDefaults()
        }

        private fun createCommonDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "identity"
            return defaults
        }

        private fun createSmoothDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "smooth"
            return defaults
        }

        private fun createBarDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "count"
            defaults["position"] = "stack"
            return defaults
        }

        private fun createHistogramDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "bin"
            defaults["position"] = "stack"
            return defaults
        }

        private fun createContourDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "contour"
            return defaults
        }

        private fun createContourfDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "contourf"
            return defaults
        }


        private fun createCrossBarDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "identity"
            defaults["position"] = mapOf("name" to "dodge", "width" to 0.95)
            return defaults
        }

        private fun createBoxplotDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "boxplot"
            defaults["position"] = mapOf("name" to "dodge", "width" to 0.95)
            return defaults
        }

        private fun createAreaDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "identity"
            defaults["position"] = "stack"
            return defaults
        }

        private fun createDensityDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "density"
            return defaults
        }

        private fun createDensity2dDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "density2d"
            return defaults
        }

        private fun createDensity2dfDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "density2df"
            return defaults
        }

        private fun createFreqpolyDefaults(): Map<String, Any> {
            val defaults = HashMap<String, Any>()
            defaults["stat"] = "bin"
            return defaults
        }
    }
}
