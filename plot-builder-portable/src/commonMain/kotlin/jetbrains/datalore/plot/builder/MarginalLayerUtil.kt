/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.assemble.PositionalScalesUtil
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap

object MarginalLayerUtil {
    private val MARGINAL_SCALE = Scales.continuousDomain<Double>("marginal", true)
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
        scaleXProto: Scale<*>,
        scaleYProto: Scale<*>,
    ): Map<MarginSide, DoubleSpan> {

        val scaleXYByMargin = scaleXYByMargin(scaleXProto, scaleYProto)
        val layersByMargin = marginalLayersByMargin(marginalLayers)

        return layersByMargin.mapValues { (side, layers) ->
            val (marginScaleXProto, marginScaleYProto) = scaleXYByMargin.getValue(side)
            val layersByTile = listOf(layers)
            val domainXYByTile = PositionalScalesUtil.computePlotXYTransformedDomains(
                layersByTile,
                marginScaleXProto,
                marginScaleYProto,
                PlotFacets.undefined()
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
        scaleXProto: Scale<*>,
        scaleYProto: Scale<*>,
    ): Map<MarginSide, Pair<Scale<*>, Scale<*>>> {
        return mapOf(
            MarginSide.LEFT to Pair(MARGINAL_SCALE_REVERSED, scaleYProto),
            MarginSide.RIGHT to Pair(MARGINAL_SCALE, scaleYProto),
            MarginSide.TOP to Pair(scaleXProto, MARGINAL_SCALE),
            MarginSide.BOTTOM to Pair(scaleXProto, MARGINAL_SCALE_REVERSED)
        )
    }

    fun toMarginalScaleMap(scaleMap: TypedScaleMap, margin: MarginSide): TypedScaleMap {
        val m = scaleMap.map.mapValues { (aes, scale) ->
            when (margin) {
                MarginSide.LEFT, MarginSide.RIGHT -> if (Aes.isPositionalX(aes)) MARGINAL_SCALES.getValue(margin) else scale
                MarginSide.TOP, MarginSide.BOTTOM -> if (Aes.isPositionalY(aes)) MARGINAL_SCALES.getValue(margin) else scale
            }
        }
        return TypedScaleMap(m)
    }

}