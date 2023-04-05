/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.builder.assemble.GuideOptions
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.builder.presentation.FontFamilyRegistry
import jetbrains.datalore.plot.builder.scale.AxisPosition
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.plot.config.Option.Plot.COORD
import jetbrains.datalore.plot.config.Option.Plot.GUIDES
import jetbrains.datalore.plot.config.Option.Plot.THEME
import jetbrains.datalore.plot.config.PlotConfigClientSideUtil.createGuideOptionsMap
import jetbrains.datalore.plot.config.theme.ThemeConfig
import jetbrains.datalore.plot.config.transform.PlotSpecTransform
import jetbrains.datalore.plot.config.transform.migration.MoveGeomPropertiesToLayerMigration

class PlotConfigClientSide private constructor(opts: Map<String, Any>) :
    PlotConfig(
        opts,
        isClientSide = true
    ) {

    internal val fontFamilyRegistry: FontFamilyRegistry
    internal val theme: Theme
    internal val coordProvider: CoordProvider
    internal val guideOptionsMap: Map<Aes<*>, GuideOptions>

    val scaleMap: Map<Aes<*>, Scale>
    val mappersByAesNP: Map<Aes<*>, ScaleMapper<*>>

    internal val xAxisPosition: AxisPosition
    internal val yAxisPosition: AxisPosition

    init {
        fontFamilyRegistry = FontFamilyRegistryConfig(this).createFontFamilyRegistry()
        theme = ThemeConfig(getMap(THEME), fontFamilyRegistry).theme

        val mappersByAes = PlotConfigScaleMappers.createMappers(
            layerConfigs,
            transformByAes,
            mapperProviderByAes,
        )

        // ToDo: First transform data then create scales.
        scaleMap = PlotConfigScales.createScales(
            layerConfigs,
            transformByAes,
            mappersByAes,
            scaleProviderByAes
        )

        // Use only Non-positional mappers.
        mappersByAesNP = mappersByAes.filterKeys { !Aes.isPositional(it) }

        val preferredCoordProvider: CoordProvider? = layerConfigs
            .firstNotNullOfOrNull { (it.geomProto as GeomProtoClientSide).preferredCoordinateSystem(it) }

        val defaultCoordProvider = preferredCoordProvider ?: CoordProviders.cartesian()
        val coordProvider = CoordConfig.create(
            get(COORD),
            transformByAes.getValue(Aes.X),
            transformByAes.getValue(Aes.Y),
            defaultCoordProvider
        )

        this.coordProvider = coordProvider
        guideOptionsMap = createGuideOptionsMap(this.scaleConfigs) + createGuideOptionsMap(getMap(GUIDES))

        xAxisPosition = scaleProviderByAes.getValue(Aes.X).axisPosition
        yAxisPosition = scaleProviderByAes.getValue(Aes.Y).axisPosition
    }

    companion object {
        fun processTransform(plotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
            @Suppress("NAME_SHADOWING")
            var plotSpec = plotSpec
            val isGGBunch = !isFailure(plotSpec) && figSpecKind(plotSpec) == FigKind.GG_BUNCH_SPEC

            plotSpec = PlotSpecTransform.builderForRawSpec()
                .build()
                .apply(plotSpec)

            // migration to new schema of plot specs
            // needed to support 'saved output' in old format
            // remove after reasonable period of time (24 Sep, 2018)
            val migrations = PlotSpecTransform.builderForRawSpec()
                .change(
                    MoveGeomPropertiesToLayerMigration.specSelector(isGGBunch),
                    MoveGeomPropertiesToLayerMigration()
                )
                .build()

            plotSpec = migrations.apply(plotSpec)
            return plotSpec
        }

        fun create(
            plotSpec: Map<String, Any>,
            computationMessagesHandler: ((List<String>) -> Unit)
        ): PlotConfigClientSide {
            val computationMessages = PlotConfigUtil.findComputationMessages(plotSpec)
            if (computationMessages.isNotEmpty()) {
                computationMessagesHandler(computationMessages)
            }
            return PlotConfigClientSide(plotSpec)
        }
    }
}
