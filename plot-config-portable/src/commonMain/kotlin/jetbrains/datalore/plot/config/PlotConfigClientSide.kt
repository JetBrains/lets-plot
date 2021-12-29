/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.builder.assemble.GuideOptions
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.builder.data.OrderOptionUtil
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.plot.config.Option.Plot.COORD
import jetbrains.datalore.plot.config.Option.Plot.GUIDES
import jetbrains.datalore.plot.config.Option.Plot.THEME
import jetbrains.datalore.plot.config.PlotConfigClientSideUtil.createGuideOptionsMap
import jetbrains.datalore.plot.config.theme.ThemeConfig
import jetbrains.datalore.plot.config.transform.PlotSpecTransform
import jetbrains.datalore.plot.config.transform.migration.MoveGeomPropertiesToLayerMigration

class PlotConfigClientSide private constructor(opts: Map<String, Any>) : PlotConfig(opts) {

    internal val theme: Theme = ThemeConfig(getMap(THEME)).theme
    internal val coordProvider: CoordProvider
    internal val guideOptionsMap: Map<Aes<*>, GuideOptions>

    override val isClientSide: Boolean
        get() = true

    init {

        val preferredCoordProvider: CoordProvider? = layerConfigs
            .map { it.geomProto as GeomProtoClientSide }
            .firstOrNull { it.hasPreferredCoordinateSystem() }
            ?.preferredCoordinateSystem()
        val defaultCoordProvider = preferredCoordProvider ?: CoordProviders.cartesian()
        val coordProvider = CoordConfig.create(
            get(COORD),
            scaleMap[Aes.X].transform,
            scaleMap[Aes.Y].transform,
            defaultCoordProvider)


        this.coordProvider = coordProvider
        guideOptionsMap = createGuideOptionsMap(this.scaleConfigs) + createGuideOptionsMap(getMap(GUIDES))
    }

    override fun createLayerConfig(
        layerOptions: Map<String, Any>,
        sharedData: DataFrame,
        plotMappings: Map<*, *>,
        plotDataMeta: Map<*, *>,
        plotOrderOptions: List<OrderOptionUtil.OrderOption>
    ): LayerConfig {

        val geomName = layerOptions[Option.Layer.GEOM] as String
        val geomKind = Option.GeomName.toGeomKind(geomName)
        return LayerConfig(
            layerOptions,
            sharedData,
            plotMappings,
            plotDataMeta,
            plotOrderOptions,
            GeomProtoClientSide(geomKind),
            true
        )
    }

    companion object {
        fun processTransform(plotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
            @Suppress("NAME_SHADOWING")
            var plotSpec = plotSpec
            val isGGBunch = isGGBunchSpec(plotSpec)

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
