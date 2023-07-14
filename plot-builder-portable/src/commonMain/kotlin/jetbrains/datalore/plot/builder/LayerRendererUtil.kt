/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.annotations.Annotations
import org.jetbrains.letsPlot.core.plot.base.geom.util.YOrientationAesthetics
import org.jetbrains.letsPlot.core.plot.base.tooltip.ContextualMapping
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers
import org.jetbrains.letsPlot.core.plot.base.util.YOrientationBaseUtil

object LayerRendererUtil {

    fun createLayerRendererData(
        layer: GeomLayer,
    ): LayerRendererData {

        val aestheticMappers = PlotUtil.prepareLayerAestheticMappers(
            layer,
            xAesMapper = Mappers.IDENTITY,
            yAesMapper = Mappers.IDENTITY
        )
        val aesthetics = PlotUtil.createLayerAesthetics(
            layer,
            layer.renderedAes(considerOrientation = true),
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

        val mappedAes: Set<org.jetbrains.letsPlot.core.plot.base.Aes<*>> = layer.renderedAes().filter(layer::hasBinding).toSet()
        val pos = PlotUtil.createPositionAdjustment(layer.posProvider, aestheticsAfterOrientation)
        return LayerRendererData(
            geom = layer.geom,
            geomKind = layer.geomKind,
            aesthetics = aestheticsAfterOrientation,
            aestheticMappers = aestheticMappersAfterOrientation,
            pos = pos,
            contextualMapping = layer.createContextualMapping(),
            mappedAes = mappedAes,
            annotations = layer.createAnnotations()
        )
    }

    class LayerRendererData(
        val geom: Geom,
        val geomKind: GeomKind,
        val aesthetics: Aesthetics,
        val aestheticMappers: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, ScaleMapper<*>>,
        val pos: PositionAdjustment,
        val contextualMapping: ContextualMapping,
        val mappedAes: Set<org.jetbrains.letsPlot.core.plot.base.Aes<*>>,
        val annotations: Annotations?
    )
}
