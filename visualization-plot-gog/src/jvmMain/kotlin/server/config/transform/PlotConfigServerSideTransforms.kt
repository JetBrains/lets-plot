package jetbrains.datalore.visualization.plot.gog.server.config.transform

import jetbrains.datalore.visualization.plot.gog.config.Option.Layer
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot
import jetbrains.datalore.visualization.plot.gog.config.transform.PlotSpecTransform
import jetbrains.datalore.visualization.plot.gog.config.transform.SpecSelector
import jetbrains.datalore.visualization.plot.gog.config.transform.migration.MoveGeomPropertiesToLayerMigration

object PlotConfigServerSideTransforms {
    fun migrationTransform(): PlotSpecTransform {
        // ToDo: remove after all input is updated (demo, test, sci ide)
        return PlotSpecTransform.builderForRawSpec()
                .change(MoveGeomPropertiesToLayerMigration.specSelector(false), MoveGeomPropertiesToLayerMigration())
                .build()
    }

    fun entryTransform(): PlotSpecTransform {
        return PlotSpecTransform.builderForRawSpec()
                .change(SpecSelector.of(Plot.DATA), NumericDataVectorSpecChange())
                .change(SpecSelector.of(Plot.LAYERS, Layer.DATA), NumericDataVectorSpecChange())
                .change(SpecSelector.of(Plot.LAYERS, Layer.GEOM, Layer.DATA), NumericDataVectorSpecChange()) // ToDo: remove (and tests)
                .change(ImageTranscodeSpecChange.specSelector(), ImageTranscodeSpecChange())
                .change(ReplaceDataVectorsInAesMappingChange.specSelector(), ReplaceDataVectorsInAesMappingChange())
                .change(LonLatSpecInMappingSpecChange.specSelector(), LonLatSpecInMappingSpecChange())
                .change(GeoDataFrameMappingChange.specSelector(), GeoDataFrameMappingChange())
                .change(GeoPositionMappingChange.specSelector(), GeoPositionMappingChange())
                .build()
    }
}
