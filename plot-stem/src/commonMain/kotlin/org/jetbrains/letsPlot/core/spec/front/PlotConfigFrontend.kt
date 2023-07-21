/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.assemble.GuideOptions
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProviders
import org.jetbrains.letsPlot.core.plot.builder.presentation.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.builder.scale.AxisPosition
import org.jetbrains.letsPlot.core.spec.FigKind
import org.jetbrains.letsPlot.core.spec.Option.Plot.COORD
import org.jetbrains.letsPlot.core.spec.Option.Plot.GUIDES
import org.jetbrains.letsPlot.core.spec.Option.Plot.THEME
import org.jetbrains.letsPlot.core.spec.PlotConfigUtil
import org.jetbrains.letsPlot.core.spec.config.CoordConfig
import org.jetbrains.letsPlot.core.spec.config.FontFamilyRegistryConfig
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.spec.config.ThemeConfig
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontendUtil.createGuideOptionsMap
import org.jetbrains.letsPlot.core.spec.transform.PlotSpecTransform
import org.jetbrains.letsPlot.core.spec.transform.migration.MoveGeomPropertiesToLayerMigration

class PlotConfigFrontend private constructor(opts: Map<String, Any>) :
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

        val preferredCoordProvider: CoordProvider? = layerConfigs.firstNotNullOfOrNull {
            it.geomProto.preferredCoordinateSystem(it)
        }

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
        ): PlotConfigFrontend {
            val computationMessages = PlotConfigUtil.findComputationMessages(plotSpec)
            if (computationMessages.isNotEmpty()) {
                computationMessagesHandler(computationMessages)
            }
            return PlotConfigFrontend(plotSpec)
        }
    }
}
