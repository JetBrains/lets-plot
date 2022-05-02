/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.YOrientationAesthetics
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.util.YOrientationBaseUtil

object LayerRendererUtil {

    fun createLayerRendererData(
        layer: GeomLayer,
        xAesMapper: ScaleMapper<Double>,
        yAesMapper: ScaleMapper<Double>,
    ): LayerRendererData {

        val aestheticMappers = PlotUtil.prepareLayerAestheticMappers(
            layer,
            xAesMapper, yAesMapper
        )
        val aesthetics = PlotUtil.createLayerAesthetics(
            layer,
            layer.renderedAes(),
            aestheticMappers,
        )

        val aestheticMappersAfterOrientation = aestheticMappers.let {
            when (layer.isYOrientation) {
                true -> YOrientationBaseUtil.flipAesKeys(it)
                false -> it
            }
        }

        val aestheticsAfterOrientation = aesthetics.let {
            when (layer.isYOrientation) {
                true -> YOrientationAesthetics(it)
                false -> it
            }
        }

        val pos = PlotUtil.createLayerPos(layer, aestheticsAfterOrientation)
        return LayerRendererData(
            layer,
            aestheticsAfterOrientation,
            aestheticMappersAfterOrientation,
            pos
        )
    }

    class LayerRendererData(
        private val layer: GeomLayer,
        val aesthetics: Aesthetics,
        val aestheticMappers: Map<Aes<*>, ScaleMapper<*>>,
        val pos: PositionAdjustment
    ) {
        val geom: Geom = layer.geom
        val geomKind: GeomKind = layer.geomKind
        val contextualMapping: ContextualMapping = layer.contextualMapping
        val mappedAes: Set<Aes<*>> = layer.renderedAes().filter(layer::hasBinding).toSet()
    }
}
