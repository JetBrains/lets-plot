/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.jetbrains.letsPlot.core.plot.builder.assemble.GuideKey
import org.jetbrains.letsPlot.core.plot.builder.assemble.GuideOptionsList
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotAssembler
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotGeomTiles
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProviders
import org.jetbrains.letsPlot.core.plot.builder.coord.PolarCoordProvider
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.SpecOverride
import org.jetbrains.letsPlot.core.spec.StatProto
import org.jetbrains.letsPlot.core.spec.config.CoordConfig
import org.jetbrains.letsPlot.core.spec.config.GuideConfig
import org.jetbrains.letsPlot.core.spec.config.OptionsAccessor.Companion.over
import org.jetbrains.letsPlot.core.spec.config.PlotConfigTransforms
import org.jetbrains.letsPlot.core.spec.config.ScaleConfig
import org.jetbrains.letsPlot.core.spec.conversion.AesOptionConversion
import org.jetbrains.letsPlot.core.spec.front.tiles.PlotTilesConfig

object PlotConfigFrontendUtil {
    internal fun createGuideOptions(
        scaleConfigs: List<ScaleConfig<*>>,
        guideOptionsList: Map<String, Any>,
        aopConversion: AesOptionConversion
    ): Map<GuideKey, GuideOptionsList> {
        val scaleGuides = createGuideOptionsMap(scaleConfigs, aopConversion)
        val plotGuides = createGuideOptionsMap(guideOptionsList, aopConversion)

        return (scaleGuides.asSequence() + plotGuides.asSequence())
            .groupBy({ it.key }, { it.value })
            .mapValues { (_, values) ->
                values.fold(GuideOptionsList()) { acc, list -> acc + list }
            }
    }

    private fun createGuideOptionsMap(
        scaleConfigs: List<ScaleConfig<*>>,
        aopConversion: AesOptionConversion
    ): Map<GuideKey, GuideOptionsList> {
        val guideOptionsByAesName = HashMap<GuideKey, GuideOptionsList>()
        for (scaleConfig in scaleConfigs) {
            if (scaleConfig.hasGuideOptions()) {
                val guideOptions = scaleConfig.getGuideOptions().createGuideOptions(aopConversion)
                guideOptionsByAesName.getOrPut(GuideKey.fromAes(scaleConfig.aes), ::GuideOptionsList).add(guideOptions)
            }
        }
        return guideOptionsByAesName
    }

    private fun createGuideOptionsMap(
        guideOptionsList: Map<String, Any>,
        aopConversion: AesOptionConversion
    ): Map<GuideKey, GuideOptionsList> {
        val guideOptionsByName = HashMap<GuideKey, GuideOptionsList>()
        for ((key, value) in guideOptionsList) {
            val guideKey = when (key) {
                in Option.Mapping.REAL_AES_OPTION_NAMES -> GuideKey.fromAes(Option.Mapping.toAes(key))
                Option.Layer.DEFAULT_LEGEND_GROUP_NAME -> GuideKey.fromName("")
                else -> GuideKey.fromName(key)
            }
            val guideOptions = GuideConfig.create(value).createGuideOptions(aopConversion)
            guideOptionsByName.getOrPut(guideKey, ::GuideOptionsList).add(guideOptions)
        }
        return guideOptionsByName
    }

    internal fun createMappersAndScalesBeforeFacets(config: PlotConfigFrontend): Pair<Map<Aes<*>, ScaleMapper<*>>, Map<Aes<*>, Scale>> {
        val layerConfigs = config.layerConfigs

        val transformByAes = PlotConfigTransforms.createTransforms(
            layerConfigs,
            config.scaleProviderByAes,
            config.mapperProviderByAes,
            excludeStatVariables = false   // Frontend - take in account "stat" variables
        )

        val mappersByAes = PlotConfigScaleMappers.createMappers(
            layerConfigs,
            transformByAes,
            config.mapperProviderByAes,
        )

        val scaleByAes = PlotConfigScales.createScales(
            layerConfigs,
            transformByAes,
            mappersByAes,
            config.scaleProviderByAes,
            config.guideOptionsMap
        )
        return Pair(mappersByAes, scaleByAes)
    }

    fun createPlotGeomTiles(
        config: PlotConfigFrontend,
        sharedContinuousDomainX: DoubleSpan? = null,
        sharedContinuousDomainY: DoubleSpan? = null,
    ): PlotGeomTiles {
        // Scale "before facets".
        val (mappersByAesNP, scaleByAesBeforeFacets) =
            createMappersAndScalesBeforeFacets(config).let { (mappers, scales) ->
                // Adjust domains of continuous scales when axis are shared between plots in a composite figure.
                val scalesAdjusted: Map<Aes<*>, Scale> = scales.mapValues { (aes, scale) ->
                    if (aes == Aes.X && sharedContinuousDomainX != null) {
                        scale.with().continuousTransform(
                            Transforms.continuousWithLimits(
                                scale.transform as ContinuousTransform,
                                sharedContinuousDomainX.toPair()
                            )
                        ).build()
                    } else if (aes == Aes.Y && sharedContinuousDomainY != null) {
                        scale.with().continuousTransform(
                            Transforms.continuousWithLimits(
                                scale.transform as ContinuousTransform,
                                sharedContinuousDomainY.toPair()
                            )
                        ).build()
                    } else {
                        scale
                    }
                }

                // Take only non-positional mappers
                Pair(
                    mappers.filterKeys { !Aes.isPositional(it) },
                    scalesAdjusted
                )
            }

        // Coord provider
        val statPreferredCoordProvider: CoordProvider? = config.layerConfigs.firstNotNullOfOrNull {
            StatProto.preferredCoordinateSystem(it.statKind)
        }
        val geomPreferredCoordProvider: CoordProvider? = config.layerConfigs.firstNotNullOfOrNull {
            it.geomProto.preferredCoordinateSystem(it)
        }

        val defaultCoordProvider =
            statPreferredCoordProvider ?: geomPreferredCoordProvider ?: CoordProviders.cartesian()

        val coordProvider = CoordConfig.createCoordProvider(
            config[Option.Plot.COORD],
            scaleByAesBeforeFacets.getValue(Aes.X).transform,
            scaleByAesBeforeFacets.getValue(Aes.Y).transform,
            defaultCoordProvider
        ).let { coordProvider ->
            @Suppress("UNCHECKED_CAST")
            config[Option.Plot.SPEC_OVERRIDE]?.let { specOverride ->
                val accessor = over(specOverride as Map<String, Any>)
                val xlimOverride = accessor.getNumQPairDef(SpecOverride.COORD_XLIM_TRANSFORMED, Pair(null, null))
                    .let { Pair(it.first?.toDouble(), it.second?.toDouble()) }
                val ylimOverride = accessor.getNumQPairDef(SpecOverride.COORD_YLIM_TRANSFORMED, Pair(null, null))
                    .let { Pair(it.first?.toDouble(), it.second?.toDouble()) }

                coordProvider
                    .withXlimOverride(xlimOverride)
                    .withYlimOverride(ylimOverride)
            } ?: coordProvider
        }.let {
            if (it.isPolar) {
                (it as PolarCoordProvider).withHScaleContinuous(scaleByAesBeforeFacets.getValue(Aes.X).isContinuousDomain)
            } else {
                it
            }
        }

        return PlotTilesConfig.createGeomTiles(
            config.layerConfigs,
            config.facets,
            coordProvider,
            scaleByAesBeforeFacets,
            mappersByAesNP,
            config.theme,
            config.theme.fontFamilyRegistry,
        )
    }

    fun createPlotAssembler(
        config: PlotConfigFrontend,
        sharedContinuousDomainX: DoubleSpan? = null,
        sharedContinuousDomainY: DoubleSpan? = null,
    ): PlotAssembler {

        val plotGeomTiles = createPlotGeomTiles(
            config,
            sharedContinuousDomainX,
            sharedContinuousDomainY
        )
        return PlotAssembler(
            plotGeomTiles,
            config.facets,
            config.xAxisPosition,
            config.yAxisPosition,
            config.theme,
            title = config.title,
            subtitle = config.subtitle,
            caption = config.caption,
            guideOptionsMap = config.guideOptionsMap,
            plotSpecId = config.specId,
            tz = config.tz,
        )
    }
}
