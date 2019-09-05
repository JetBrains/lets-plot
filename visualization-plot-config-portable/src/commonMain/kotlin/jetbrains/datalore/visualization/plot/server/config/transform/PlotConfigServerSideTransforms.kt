package jetbrains.datalore.visualization.plot.server.config.transform

import jetbrains.datalore.visualization.plot.config.Option.Layer
import jetbrains.datalore.visualization.plot.config.Option.Plot
import jetbrains.datalore.visualization.plot.config.transform.PlotSpecTransform
import jetbrains.datalore.visualization.plot.config.transform.SpecSelector
import jetbrains.datalore.visualization.plot.config.transform.migration.MoveGeomPropertiesToLayerMigration

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
                .change(SpecSelector.of(Plot.LAYERS), ImageTranscodeSpecChange())
                .change(ReplaceDataVectorsInAesMappingChange.specSelector(), ReplaceDataVectorsInAesMappingChange())
                .change(LonLatSpecInMappingSpecChange.specSelector(), LonLatSpecInMappingSpecChange())
                .change(GeoDataFrameMappingChange.specSelector(), GeoDataFrameMappingChange())
                .change(GeoPositionMappingChange.specSelector(), GeoPositionMappingChange())
                .build()
    }
}
