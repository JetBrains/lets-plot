/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.builder.assemble.GuideOptions
import jetbrains.datalore.plot.builder.assemble.TypedScaleProviderMap
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.plot.config.Option.Plot.COORD
import jetbrains.datalore.plot.config.Option.Plot.THEME
import jetbrains.datalore.plot.config.PlotConfigClientSideUtil.createGuideOptionsMap
import jetbrains.datalore.plot.config.geo.GeometryFromGeoDataFrameChange
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

        val coord = CoordConfig.create(get(COORD)!!)
        var coordProvider = coord.coord
        if (!hasOwn(COORD)) {
            // if coord wasn't set explicitly then geom can provide its own preferred coord system
            for (layerConfig in layerConfigs) {
                val geomProtoClientSide = layerConfig.geomProto as GeomProtoClientSide
                if (geomProtoClientSide.hasPreferredCoordinateSystem()) {
                    coordProvider = geomProtoClientSide.preferredCoordinateSystem()
                }
            }
        }
        this.coordProvider = coordProvider
        guideOptionsMap = createGuideOptionsMap(this.scaleConfigs)
    }

    override fun createLayerConfig(
        layerOptions: Map<*, *>,
        sharedData: DataFrame?,
        plotMappings: Map<*, *>,
        plotDiscreteAes: Set<*>,
        scaleProviderByAes: TypedScaleProviderMap
    ): LayerConfig {

        val geomName = layerOptions[Option.Layer.GEOM] as String
        val geomKind = Option.GeomName.toGeomKind(geomName)
        return LayerConfig(
            layerOptions,
            sharedData!!,
            plotMappings,
            plotDiscreteAes,
            GeomProtoClientSide(geomKind),
            StatProto(),
            scaleProviderByAes, true
        )
    }

    companion object {
        fun processTransform(plotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
            @Suppress("NAME_SHADOWING")
            var plotSpec = plotSpec
            val isGGBunch = isGGBunchSpec(plotSpec)

            plotSpec = PlotSpecTransform.builderForRawSpec()
                .change(GeometryFromGeoDataFrameChange.specSelector(isGGBunch), GeometryFromGeoDataFrameChange())
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

        /**
         * Tests only
         */
        fun create(plotSpec: Map<String, Any>): PlotConfigClientSide {
            return PlotConfigClientSide(plotSpec)
        }
    }
}
