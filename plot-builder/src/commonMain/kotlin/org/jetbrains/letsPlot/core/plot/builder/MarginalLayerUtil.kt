/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.scale.Scales
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.assemble.PositionalScalesUtil

object MarginalLayerUtil {
    private val MARGINAL_SCALE = Scales.continuousDomain("marginal", true)
    private val MARGINAL_SCALE_REVERSED = MARGINAL_SCALE.with().continuousTransform(Transforms.REVERSE).build()
    private val MARGINAL_SCALES = mapOf(
        MarginSide.LEFT to MARGINAL_SCALE_REVERSED,
        MarginSide.TOP to MARGINAL_SCALE,
        MarginSide.RIGHT to MARGINAL_SCALE,
        MarginSide.BOTTOM to MARGINAL_SCALE_REVERSED,
    )

    fun marginalLayersByMargin(marginalLayers: List<GeomLayer>): Map<MarginSide, List<GeomLayer>> {
        return marginalLayers
            .fold(LinkedHashMap<MarginSide, MutableList<GeomLayer>>()) { map, layer ->
                map.getOrPut(layer.marginalSide, ::ArrayList).add(layer)
                map
            }
    }

    fun marginalDomainByMargin(
        marginalLayers: List<GeomLayer>,
        scaleXProto: Scale,
        scaleYProto: Scale,
    ): Map<MarginSide, DoubleSpan> {

        val scaleXYByMargin = scaleXYByMargin(scaleXProto, scaleYProto)
        val layersByMargin = marginalLayersByMargin(marginalLayers)

        return layersByMargin.mapValues { (side, layers) ->
            val (marginScaleXProto, marginScaleYProto) = scaleXYByMargin.getValue(side)

            val domainXYByTile = PositionalScalesUtil.computePlotXYTransformedDomains(
                listOf(layers),
                listOf(marginScaleXProto),
                listOf(marginScaleYProto),
                PlotFacets.UNDEFINED
            )

            // All tiles share the same domain.
            val (xDomain, yDomain) = domainXYByTile.first()
            when (side) {
                MarginSide.LEFT, MarginSide.RIGHT -> xDomain
                MarginSide.TOP, MarginSide.BOTTOM -> yDomain
            }
        }
    }

    private fun scaleXYByMargin(
        scaleXProto: Scale,
        scaleYProto: Scale,
    ): Map<MarginSide, Pair<Scale, Scale>> {
        return mapOf(
            MarginSide.LEFT to Pair(MARGINAL_SCALE_REVERSED, scaleYProto),
            MarginSide.RIGHT to Pair(MARGINAL_SCALE, scaleYProto),
            MarginSide.TOP to Pair(scaleXProto, MARGINAL_SCALE),
            MarginSide.BOTTOM to Pair(scaleXProto, MARGINAL_SCALE_REVERSED)
        )
    }

    fun toMarginalScaleMap(
        scaleMap: Map<Aes<*>, Scale>,
        margin: MarginSide,
        flipOrientation: Boolean
    ): Map<Aes<*>, Scale> {

        fun isXAxis(aes: Aes<*>): Boolean {
            return when (flipOrientation) {
                true -> Aes.isPositionalY(aes)
                false -> Aes.isPositionalX(aes)
            }
        }

        fun isYAxis(aes: Aes<*>): Boolean {
            return when (flipOrientation) {
                true -> Aes.isPositionalX(aes)
                false -> Aes.isPositionalY(aes)
            }
        }

        return scaleMap.mapValues { (aes, scale) ->
            when (margin) {
                MarginSide.LEFT, MarginSide.RIGHT -> if (isXAxis(aes)) MARGINAL_SCALES.getValue(margin) else scale
                MarginSide.TOP, MarginSide.BOTTOM -> if (isYAxis(aes)) MARGINAL_SCALES.getValue(margin) else scale
            }
        }
    }
}