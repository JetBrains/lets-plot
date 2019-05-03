package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.COORD
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.THEME
import jetbrains.datalore.visualization.plot.gog.config.PlotConfigUtil.createGuideOptionsMap
import jetbrains.datalore.visualization.plot.gog.config.theme.ThemeConfig
import jetbrains.datalore.visualization.plot.gog.config.transform.PlotSpecTransform
import jetbrains.datalore.visualization.plot.gog.config.transform.encode.DataSpecEncodeTransforms
import jetbrains.datalore.visualization.plot.gog.config.transform.migration.MoveGeomPropertiesToLayerMigration
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.plot.GuideOptions
import jetbrains.datalore.visualization.plot.gog.plot.assemble.TypedScaleProviderMap
import jetbrains.datalore.visualization.plot.gog.plot.coord.CoordProvider
import jetbrains.datalore.visualization.plot.gog.plot.theme.Theme

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
                val geomProvider = layerConfig.geomProvider
                if (geomProvider.hasPreferredCoordinateSystem()) {
                    coordProvider = geomProvider.preferredCoordinateSystem
                }
            }
        }
        this.coordProvider = coordProvider
        guideOptionsMap = createGuideOptionsMap(createScaleConfigs())
    }

    override fun createLayerConfig(
            layerOptions: Map<*, *>, sharedData: DataFrame?, plotMapping: Map<*, *>,
            scaleProviderByAes: TypedScaleProviderMap): LayerConfig {

        return LayerConfig(
                layerOptions,
                sharedData!!,
                plotMapping,
                StatProto(),
                scaleProviderByAes, true)
    }

    companion object {
        fun processTransform(plotSpec: Map<String, Any>): Map<String, Any> {
            var plotSpec = plotSpec
            val isGGBunch = isGGBunchSpec(plotSpec)

            // migration to new schema of plot spces
            // needed to support 'saved output' in old format
            // remove after reasonable priod of time (24 Sep, 2018)
            val migrations = PlotSpecTransform.builderForRawSpec()
                    .change(MoveGeomPropertiesToLayerMigration.specSelector(isGGBunch), MoveGeomPropertiesToLayerMigration())
                    .build()
            plotSpec = migrations.apply(plotSpec)

            return DataSpecEncodeTransforms.clientSideDecode(isGGBunch).apply(plotSpec)
        }

        internal fun create(plotSpec: Map<String, Any>): PlotConfigClientSide {
            return PlotConfigClientSide(plotSpec)
        }
    }
}
