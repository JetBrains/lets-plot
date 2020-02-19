/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.interact.MappedDataAccess

object LayerRendererUtil {

    fun createLayerRendererData(layer: GeomLayer,
                                sharedNumericMappers: Map<Aes<Double>, (Double?) -> Double?>,
                                overallNumericDomains: Map<Aes<Double>, ClosedRange<Double>>
    ): LayerRendererData {

        val aestheticMappers =
            PlotUtil.prepareLayerAestheticMappers(layer, sharedNumericMappers)
        val aesthetics = PlotUtil.createLayerAesthetics(
            layer,
            aestheticMappers,
            overallNumericDomains
        )
        val pos = PlotUtil.createLayerPos(layer, aesthetics)
        return LayerRendererData(
            layer.geom,
            layer.geomKind,
            aesthetics,
            aestheticMappers,
            pos,
            layer.dataAccess
        )
    }

    class LayerRendererData(
        val geom: Geom,
        val geomKind: GeomKind,
        val aesthetics: Aesthetics,
        val aestheticMappers: Map<Aes<*>, (Double?) -> Any?>,
        val pos: PositionAdjustment,
        val dataAccess: MappedDataAccess
    )
}
