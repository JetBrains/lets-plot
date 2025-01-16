/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform

import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.CorrPlotSpecChange
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.QQPlotSpecChange
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.WaterfallPlotSpecChange
import org.jetbrains.letsPlot.core.spec.transform.PlotSpecTransform
import org.jetbrains.letsPlot.core.spec.transform.SpecSelector
import org.jetbrains.letsPlot.core.spec.transform.migration.MoveGeomPropertiesToLayerMigration

object PlotConfigBackendTransforms {
    fun migrationTransform(): PlotSpecTransform {
        return PlotSpecTransform.builderForRawSpec()
            .change(
                MoveGeomPropertiesToLayerMigration.specSelector(),
                MoveGeomPropertiesToLayerMigration()
            )
            .build()
    }

    fun entryTransform(): PlotSpecTransform {
        return PlotSpecTransform.builderForRawSpec()
            .change(
                SpecSelector.of(PlotBase.DATA),
                NumericDataVectorSpecChange()
            )
            .change(
                SpecSelector.of(Plot.LAYERS, PlotBase.DATA),
                NumericDataVectorSpecChange()
            )
            .change(
                SpecSelector.of(Plot.LAYERS, Layer.GEOM, PlotBase.DATA),
                NumericDataVectorSpecChange()
            )
            .change(
                ReplaceDataVectorsInAesMappingChange.specSelector(),
                ReplaceDataVectorsInAesMappingChange()
            )
            .build()
    }

    fun bistroTransform(): PlotSpecTransform {
        return PlotSpecTransform.builderForRawSpec()
            .change(
                CorrPlotSpecChange.specSelector(),
                CorrPlotSpecChange()
            )
            .change(
                QQPlotSpecChange.specSelector(),
                QQPlotSpecChange()
            )
            .change(
                WaterfallPlotSpecChange.specSelector(),
                WaterfallPlotSpecChange()
            )
            .build()
    }

}
