/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.PointDimensionsUtil
import jetbrains.datalore.plot.base.geom.WithHeight
import jetbrains.datalore.plot.base.geom.WithWidth
import jetbrains.datalore.plot.base.geom.util.YOrientationAesthetics
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.PlotUtil
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.max
import kotlin.math.min

internal object PositionalScalesUtil {
    /**
     * Computers X/Y ranges of transformed input series.
     *
     * @return list of pairs (x-domain, y-domain).
     *          Elements in this list match corresponding elements in the `layersByTile` list.
     */
    fun computePlotXYTransformedDomains(
        layersByTile: List<List<GeomLayer>>,
        xScaleProto: Scale,
        yScaleProto: Scale,
        facets: PlotFacets
    ): List<Pair<DoubleSpan, DoubleSpan>> {
        var xInitialDomain: DoubleSpan? = RangeUtil.initialRange(xScaleProto.transform)
        var yInitialDomain: DoubleSpan? = RangeUtil.initialRange(yScaleProto.transform)

        var xDomains = ArrayList<DoubleSpan?>()
        val yDomains = ArrayList<DoubleSpan?>()
        for (tileLayers in layersByTile) {
            val (xDomain, yDomain) = computeTileXYDomains(
                tileLayers,
                xInitialDomain,
                yInitialDomain
            )

            xDomains.add(xDomain)
            yDomains.add(yDomain)
        }

        val adjustedXDomains: List<DoubleSpan?> = facets.adjustHDomains(xDomains)
        val adjustedYDomains: List<DoubleSpan?> = facets.adjustVDomains(yDomains)

        val finalizedXDomains: List<DoubleSpan> = finalizeDomains(
            Aes.X,
            xScaleProto,
            adjustedXDomains,
            layersByTile,
            facets.freeHScale
        )
        val finalizedYDomains: List<DoubleSpan> = finalizeDomains(
            Aes.Y,
            yScaleProto,
            adjustedYDomains,
            layersByTile,
            facets.freeVScale
        )

        return finalizedXDomains.zip(finalizedYDomains)
    }

    private fun finalizeDomains(
        aes: Aes<Double>,
        scaleProto: Scale,
        domains: List<DoubleSpan?>,
        layersByTile: List<List<GeomLayer>>,
        freeScale: Boolean
    ): List<DoubleSpan> {

        return when {
            freeScale -> {
                // Each tile has its own domain
                domains.mapIndexed { i, v ->
                    // 'expand' ranges and include '0' if necessary
                    val domainExpanded = RangeUtil.expandRange(v, aes, scaleProto, layersByTile[i])
                    SeriesUtil.ensureApplicableRange(domainExpanded)
                }
            }

            else -> {
                // One domain for all tiles.
                val domainOverall = domains.filterNotNull().reduceOrNull { r0, r1 ->
                    RangeUtil.updateRange(r0, r1)!!
                }
                val preferableNullDomainOverall = layersByTile[0]
                    .map { it.preferableNullDomain(aes) }
                    .reduceOrNull { r0, r1 -> RangeUtil.updateRange(r0, r1)!! }

                // 'expand' ranges and include '0' if necessary
                val domainExpanded = RangeUtil.expandRange(domainOverall, aes, scaleProto, layersByTile[0])
                val domain = SeriesUtil.ensureApplicableRange(domainExpanded, preferableNullDomainOverall)

                layersByTile.map { domain }
            }
        }
    }

    private fun computeTileXYDomains(
        layers: List<GeomLayer>,
        xInitialDomain: DoubleSpan?,
        yInitialDomain: DoubleSpan?
    ): Pair<DoubleSpan?, DoubleSpan?> {
        val positionaDryRunAestheticsByLayer: Map<GeomLayer, Aesthetics> = layers.associateWith {
            positionalDryRunAesthetics(it)
        }

        var xDomainOverall: DoubleSpan? = null
        var yDomainOverall: DoubleSpan? = null

        // Use dry-run aesthetics to estimate ranges.
        for ((layer, aesthetics) in positionaDryRunAestheticsByLayer) {

            // adjust X/Y range with 'pos adjustment' and 'expands'
            val xyRanges = computeLayerDryRunXYRanges(layer, aesthetics)

            val xRangeLayer = RangeUtil.updateRange(xInitialDomain, xyRanges.first)
            val yRangeLayer = RangeUtil.updateRange(yInitialDomain, xyRanges.second)

            xDomainOverall = RangeUtil.updateRange(xRangeLayer, xDomainOverall)
            yDomainOverall = RangeUtil.updateRange(yRangeLayer, yDomainOverall)
        }

        return Pair(xDomainOverall, yDomainOverall)
    }

    private fun positionalDryRunAesthetics(layer: GeomLayer): Aesthetics {
        val aesList = layer.renderedAes(considerOrientation = true).filter {
            Aes.affectingScaleX(it) ||
                    Aes.affectingScaleY(it) ||
                    it == Aes.HEIGHT ||
                    it == Aes.WIDTH
        }

        val mappers = aesList.associateWith { Mappers.IDENTITY }
        return PlotUtil.createLayerAesthetics(layer, aesList, mappers)
    }

    private fun computeLayerDryRunXYRanges(
        layer: GeomLayer,
        aesthetics: Aesthetics
    ): Pair<DoubleSpan?, DoubleSpan?> {

        @Suppress("NAME_SHADOWING")
        val rangesAfterPosAdjustment = when (layer.isYOrientation) {
            true -> YOrientationAesthetics(aesthetics)
            false -> aesthetics
        }.let { aesthetics ->
            val geomCtx = GeomContextBuilder().aesthetics(aesthetics).build()
            val rangesXY =
                computeLayerDryRunXYRangesAfterPosAdjustment(layer, aesthetics, geomCtx)

            // return to "normal" orientation
            when (layer.isYOrientation) {
                true -> Pair(rangesXY.second, rangesXY.first)
                false -> rangesXY
            }
        }

        val geomCtx = GeomContextBuilder().aesthetics(aesthetics).build()
        val (xRangeAfterSizeExpand, yRangeAfterSizeExpand) =
            computeLayerDryRunXYRangesAfterSizeExpand(layer, aesthetics, geomCtx)

        var rangeX = rangesAfterPosAdjustment.first
        if (rangeX == null) {
            rangeX = xRangeAfterSizeExpand
        } else if (xRangeAfterSizeExpand != null) {
            rangeX = rangeX.union(xRangeAfterSizeExpand)
        }

        var rangeY = rangesAfterPosAdjustment.second
        if (rangeY == null) {
            rangeY = yRangeAfterSizeExpand
        } else if (yRangeAfterSizeExpand != null) {
            rangeY = rangeY.union(yRangeAfterSizeExpand)
        }

        return Pair(rangeX, rangeY)
    }

    private fun computeLayerDryRunXYRangesAfterPosAdjustment(
        layer: GeomLayer, aes: Aesthetics, geomCtx: GeomContext
    ): Pair<DoubleSpan?, DoubleSpan?> {
        val posAesX = Aes.affectingScaleX(layer.renderedAes())
        val posAesY = Aes.affectingScaleY(layer.renderedAes())

        val pos = PlotUtil.createPositionAdjustment(layer.posProvider, aes)
        if (pos.isIdentity) {
            // simplified ranges
            val rangeX = RangeUtil.combineRanges(posAesX, aes)
            val rangeY = RangeUtil.combineRanges(posAesY, aes)
            return Pair(rangeX, rangeY)
        }

        var adjustedMinX = 0.0
        var adjustedMaxX = 0.0
        var adjustedMinY = 0.0
        var adjustedMaxY = 0.0
        var rangesInited = false

        val cardinality = posAesX.size * posAesY.size
        val px = arrayOfNulls<Double>(cardinality)
        val py = arrayOfNulls<Double>(cardinality)
        for (p in aes.dataPoints()) {
            var i = -1
            for (aesX in posAesX) {
                val valX = p.numeric(aesX)
                for (aesY in posAesY) {
                    val valY = p.numeric(aesY)
                    i++
                    px[i] = valX
                    py[i] = valY
                }
            }

            while (i >= 0) {
                if (px[i] != null && py[i] != null) {
                    val x = px[i]
                    val y = py[i]
                    if (SeriesUtil.isFinite(x) && SeriesUtil.isFinite(y)) {
                        val newLoc = pos.translate(DoubleVector(x!!, y!!), p, geomCtx)
                        val adjustedX = newLoc.x
                        val adjustedY = newLoc.y
                        if (rangesInited) {
                            adjustedMinX = min(adjustedX, adjustedMinX)
                            adjustedMaxX = max(adjustedX, adjustedMaxX)
                            adjustedMinY = min(adjustedY, adjustedMinY)
                            adjustedMaxY = max(adjustedY, adjustedMaxY)
                        } else {
                            adjustedMaxX = adjustedX
                            adjustedMinX = adjustedMaxX
                            adjustedMaxY = adjustedY
                            adjustedMinY = adjustedMaxY
                            rangesInited = true
                        }
                    }
                }
                i--
            }
        }

        // X range
        val xRange = if (rangesInited)
            DoubleSpan(adjustedMinX, adjustedMaxX)
        else
            null

        val yRange = if (rangesInited)
            DoubleSpan(adjustedMinY, adjustedMaxY)
        else
            null
        return Pair(xRange, yRange)
    }

    private fun computeLayerDryRunXYRangesAfterSizeExpand(
        layer: GeomLayer,
        aesthetics: Aesthetics,
        geomCtx: GeomContext
    ): Pair<DoubleSpan?, DoubleSpan?> {

        val (widthAxis, heightAxis) = when (layer.isYOrientation) {
            true -> Aes.Y to Aes.X
            false -> Aes.X to Aes.Y
        }

        val geom = layer.geom
        val renderedAes = layer.renderedAes()

        val xy = mapOf(
            widthAxis to when {
                geom is WithWidth -> {
                    val resolution = geomCtx.getResolution(widthAxis)
                    val isDiscrete = !layer.scaleMap.getValue(widthAxis).isContinuousDomain
                    computeLayerDryRunRangeAfterSizeExpand(aesthetics) { p ->
                        geom.widthSpan(p, widthAxis, resolution, isDiscrete)
                    }
                }

                Aes.WIDTH in renderedAes -> {
                    val resolution = geomCtx.getResolution(widthAxis)
                    computeLayerDryRunRangeAfterSizeExpand(aesthetics) { p ->
                        PointDimensionsUtil.dimensionSpan(p, widthAxis, Aes.WIDTH, resolution)
                    }
                }

                else -> null
            },
            heightAxis to when {
                geom is WithHeight -> {
                    val resolution = geomCtx.getResolution(heightAxis)
                    val isDiscrete = !layer.scaleMap.getValue(heightAxis).isContinuousDomain
                    computeLayerDryRunRangeAfterSizeExpand(aesthetics) { p ->
                        geom.heightSpan(p, heightAxis, resolution, isDiscrete)
                    }
                }

                Aes.HEIGHT in renderedAes -> {
                    val resolution = geomCtx.getResolution(heightAxis)
                    computeLayerDryRunRangeAfterSizeExpand(aesthetics) { p ->
                        PointDimensionsUtil.dimensionSpan(p, heightAxis, Aes.HEIGHT, resolution)
                    }
                }

                else -> null
            }
        )

        return Pair(xy.getValue(Aes.X), xy.getValue(Aes.Y))
    }

    private fun computeLayerDryRunRangeAfterSizeExpand(
        aesthetics: Aesthetics,
        pointSpan: (p: DataPointAesthetics) -> DoubleSpan?
    ): DoubleSpan? {
        var minMax: DoubleSpan? = null

        for (p in aesthetics.dataPoints()) {
            val span = pointSpan(p)
            minMax = SeriesUtil.span(minMax, span)
        }
        return minMax
    }

    private object RangeUtil {
        fun initialRange(transform: Transform): DoubleSpan? {
            // Init with 'scale limits'.
            return when (transform) {
                is ContinuousTransform -> {
                    val lims = ScaleUtil.transformedDefinedLimits(transform).toList().filter { it.isFinite() }
                    if (lims.isEmpty()) null
                    else DoubleSpan.encloseAll(lims)
                }

                is DiscreteTransform -> {
                    DoubleSpan.encloseAll(transform.effectiveDomainTransformed)
                }

                else -> throw IllegalStateException("Unexpected transform type: ${transform::class.simpleName}")
            }
        }

        internal fun expandRange(
            range: DoubleSpan?,
            aes: Aes<Double>,
            scale: Scale,
            layers: List<GeomLayer>
        ): DoubleSpan? {
            val includeZero = layers.any { it.rangeIncludesZero(aes) }

            @Suppress("NAME_SHADOWING")
            val range = when (includeZero) {
                true -> updateRange(DoubleSpan.singleton(0.0), range)
                false -> range
            }

            return PlotUtil.rangeWithExpand(range, scale, includeZero)
        }

        private fun updateRange(values: Iterable<Double>, wasRange: DoubleSpan?): DoubleSpan {
            val newRange = DoubleSpan.encloseAll(values)
            return when {
                wasRange == null -> newRange
                else -> wasRange.union(newRange)
            }
        }

        internal fun updateRange(range: DoubleSpan?, wasRange: DoubleSpan?): DoubleSpan? {
            @Suppress("NAME_SHADOWING")
            var range = range
            if (range != null) {
                if (wasRange != null) {
                    range = wasRange.union(range)
                }
                return range
            }
            return wasRange
        }

        internal fun combineRanges(aesList: List<Aes<Double>>, aesthetics: Aesthetics): DoubleSpan? {
            var result: DoubleSpan? = null
            for (aes in aesList) {
                val range = aesthetics.range(aes)
                if (range != null) {
                    result = result?.union(range) ?: range
                }
            }
            return result
        }
    }
}