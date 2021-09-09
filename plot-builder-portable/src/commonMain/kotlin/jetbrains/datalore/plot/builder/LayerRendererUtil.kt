/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.MappedDataAccess

object LayerRendererUtil {

    fun createLayerRendererData(
        layer: GeomLayer,
        sharedNumericMappers: Map<Aes<Double>, (Double?) -> Double?>,
    ): LayerRendererData {

        val aestheticMappers = PlotUtil.prepareLayerAestheticMappers(
            layer,
            sharedNumericMappers
        )
        val aesthetics = PlotUtil.createLayerAesthetics(
            layer,
            aestheticMappers,
        )
        val pos = PlotUtil.createLayerPos(layer, aesthetics)
        return LayerRendererData(
            layer,
            aesthetics,
            aestheticMappers,
            pos
        )
    }

    class LayerRendererData(
        layer: GeomLayer,
        val aesthetics: Aesthetics,
        val aestheticMappers: Map<Aes<*>, (Double?) -> Any?>,
        val pos: PositionAdjustment
    ) {
        val geom: Geom = layer.geom
        val geomKind: GeomKind = layer.geomKind
        val dataAccess: MappedDataAccess = layer.dataAccess
        val contextualMapping: ContextualMapping = layer.contextualMapping
    }
}
