/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.DoubleSpan
import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
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
     *          Elements is this list match corresponding elements in the `layersByTile` list.
     */
    fun computePlotXYTransformedDomains(
        layersByTile: List<List<GeomLayer>>,
        xScaleProto: Scale<Double>,
        yScaleProto: Scale<Double>,
        freeX: Boolean,
        freeY: Boolean,
    ): List<Pair<DoubleSpan, DoubleSpan>> {
        var xInitialDomain: DoubleSpan? = RangeUtil.initialRange(xScaleProto.transform)
        var yInitialDomain: DoubleSpan? = RangeUtil.initialRange(yScaleProto.transform)

        var xDomains = ArrayList<DoubleSpan>()
        val yDomains = ArrayList<DoubleSpan>()
        for (tileLayers in layersByTile) {
            val (xDomain, yDomain) = computeTileXYDomains(
                tileLayers,
                xInitialDomain,
                yInitialDomain
            )
            xDomain?.run { xDomains.add(xDomain) }
            yDomain?.run { yDomains.add(yDomain) }
        }

        val domainsX: List<DoubleSpan> = finalizeDomains(
            Aes.X,
            xScaleProto,
            xDomains,
            layersByTile,
            freeX
        )
        val domainsY: List<DoubleSpan> = finalizeDomains(
            Aes.Y,
            yScaleProto,
            yDomains,
            layersByTile,
            freeY
        )

        return domainsX.zip(domainsY)
    }

    private fun finalizeDomains(
        aes: Aes<Double>,
        scaleProto: Scale<*>,
        domains: List<DoubleSpan>,
        layersByTile: List<List<GeomLayer>>,
        freeScale: Boolean
    ): List<DoubleSpan> {

        return when (freeScale) {
            true -> {
                throw IllegalStateException("Not implemented")
            }
            else -> {
                // One domain for all tiles.
                val domainOverall = domains.reduceOrNull { r0, r1 ->
                    RangeUtil.updateRange(r0, r1)!!
                }

                // 'expand' ranges and include '0' if necessary
                val domainExpanded = RangeUtil.expandRange(domainOverall, aes, scaleProto, layersByTile[0])
                val domain = SeriesUtil.ensureApplicableRange(domainExpanded)

                layersByTile.map { domain }
            }
        }
    }

    private fun computeTileXYDomains(
        layers: List<GeomLayer>,
        xInitialDomain: ClosedRange<Double>?,
        yInitialDomain: ClosedRange<Double>?
    ): Pair<ClosedRange<Double>?, ClosedRange<Double>?> {
        val positionaDryRunAestheticsByLayer: Map<GeomLayer, Aesthetics> = layers.associateWith {
            positionaDryRunAesthetics(it)
        }

        var xDomainOverall: ClosedRange<Double>? = null
        var yDomainOverall: ClosedRange<Double>? = null
        for (layer in layers) {
            // use dry-run aesthetics to estimate ranges
            val aesthetics = positionaDryRunAestheticsByLayer.getValue(layer)
            // adjust X/Y range with 'pos adjustment' and 'expands'
            val xyRanges = computeLayerDryRunXYRanges(layer, aesthetics)

            val xRangeLayer = RangeUtil.updateRange(xInitialDomain, xyRanges.first)
            val yRangeLayer = RangeUtil.updateRange(yInitialDomain, xyRanges.second)

            xDomainOverall = RangeUtil.updateRange(xRangeLayer, xDomainOverall)
            yDomainOverall = RangeUtil.updateRange(yRangeLayer, yDomainOverall)
        }

        return Pair(xDomainOverall, yDomainOverall)
    }

    private fun positionaDryRunAesthetics(layer: GeomLayer): Aesthetics {
        val aesList = layer.renderedAes().filter {
            Aes.affectingScaleX(it) ||
                    Aes.affectingScaleY(it) ||
                    it == Aes.HEIGHT ||
                    it == Aes.WIDTH
        }

        val mappers = aesList.associateWith { Mappers.IDENTITY }

        return PlotUtil.createLayerAesthetics(layer, aesList, mappers)
    }

    private fun computeLayerDryRunXYRanges(
        layer: GeomLayer, aes: Aesthetics
    ): Pair<ClosedRange<Double>?, ClosedRange<Double>?> {
        val geomCtx = GeomContextBuilder().aesthetics(aes).build()

        val rangesAfterPosAdjustment =
            computeLayerDryRunXYRangesAfterPosAdjustment(layer, aes, geomCtx)
        val (xRangeAfterSizeExpand, yRangeAfterSizeExpand) =
            computeLayerDryRunXYRangesAfterSizeExpand(layer, aes, geomCtx)

        var rangeX = rangesAfterPosAdjustment.first
        if (rangeX == null) {
            rangeX = xRangeAfterSizeExpand
        } else if (xRangeAfterSizeExpand != null) {
            rangeX = rangeX.span(xRangeAfterSizeExpand)
        }

        var rangeY = rangesAfterPosAdjustment.second
        if (rangeY == null) {
            rangeY = yRangeAfterSizeExpand
        } else if (yRangeAfterSizeExpand != null) {
            rangeY = rangeY.span(yRangeAfterSizeExpand)
        }

        return Pair(rangeX, rangeY)
    }

    private fun computeLayerDryRunXYRangesAfterPosAdjustment(
        layer: GeomLayer, aes: Aesthetics, geomCtx: GeomContext
    ): Pair<ClosedRange<Double>?, ClosedRange<Double>?> {
        val posAesX = Aes.affectingScaleX(layer.renderedAes())
        val posAesY = Aes.affectingScaleY(layer.renderedAes())

        val pos = PlotUtil.createLayerPos(layer, aes)
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
            ClosedRange(adjustedMinX, adjustedMaxX)
        else
            null

        val yRange = if (rangesInited)
            ClosedRange(adjustedMinY, adjustedMaxY)
        else
            null
        return Pair(xRange, yRange)
    }

    private fun computeLayerDryRunXYRangesAfterSizeExpand(
        layer: GeomLayer,
        aesthetics: Aesthetics,
        geomCtx: GeomContext
    ): Pair<ClosedRange<Double>?, ClosedRange<Double>?> {
        val renderedAes = layer.renderedAes()
        val computeExpandX = renderedAes.contains(Aes.WIDTH)
        val computeExpandY = renderedAes.contains(Aes.HEIGHT)
        val rangeX = if (computeExpandX)
            computeLayerDryRunRangeAfterSizeExpand(
                Aes.X,
                Aes.WIDTH,
                aesthetics,
                geomCtx
            )
        else
            null
        val rangeY = if (computeExpandY)
            computeLayerDryRunRangeAfterSizeExpand(
                Aes.Y,
                Aes.HEIGHT,
                aesthetics,
                geomCtx
            )
        else
            null

        return Pair(rangeX, rangeY)
    }

    private fun computeLayerDryRunRangeAfterSizeExpand(
        locationAes: Aes<Double>, sizeAes: Aes<Double>, aesthetics: Aesthetics, geomCtx: GeomContext
    ): ClosedRange<Double>? {
        val locations = aesthetics.numericValues(locationAes).iterator()
        val sizes = aesthetics.numericValues(sizeAes).iterator()

        val resolution = geomCtx.getResolution(locationAes)
        val minMax = doubleArrayOf(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)

        for (i in 0 until aesthetics.dataPointCount()) {
            if (!locations.hasNext()) {
                throw IllegalStateException("Index is out of bounds: $i for $locationAes")
            }
            if (!sizes.hasNext()) {
                throw IllegalStateException("Index is out of bounds: $i for $sizeAes")
            }
            val loc = locations.next()
            val size = sizes.next()
            if (SeriesUtil.isFinite(loc) && SeriesUtil.isFinite(size)) {
                val expand = resolution * (size!! / 2)
                updateExpandedMinMax(loc!!, expand, minMax)
            }
        }

        return if (minMax[0] <= minMax[1])
            ClosedRange(minMax[0], minMax[1])
        else
            null
    }

    private fun updateExpandedMinMax(value: Double, expand: Double, expandedMinMax: DoubleArray) {
        expandedMinMax[0] = min(value - expand, expandedMinMax[0])
        expandedMinMax[1] = max(value + expand, expandedMinMax[1])
    }


    private object RangeUtil {
        fun initialRange(transform: Transform): ClosedRange<Double>? {
            // Init with 'scale limits'.
            return when (transform) {
                is ContinuousTransform -> {
                    val lims = ScaleUtil.transformedDefinedLimits(transform).toList().filter { it.isFinite() }
                    if (lims.isEmpty()) null
                    else ClosedRange.encloseAll(lims)
                }
                is DiscreteTransform -> {
                    ClosedRange.encloseAll(transform.effectiveDomainTransformed)
                }
                else -> throw IllegalStateException("Unexpected transform type: ${transform::class.simpleName}")
            }
        }

        internal fun expandRange(
            range: ClosedRange<Double>?,
            aes: Aes<Double>,
            scale: Scale<*>,
            layers: List<GeomLayer>
        ): ClosedRange<Double>? {
            val includeZero = layers.any { it.rangeIncludesZero(aes) }

            @Suppress("NAME_SHADOWING")
            val range = when (includeZero) {
                true -> updateRange(ClosedRange.singleton(0.0), range)
                false -> range
            }

            return PlotUtil.rangeWithExpand(range, scale, includeZero)
        }

        private fun updateRange(values: Iterable<Double>, wasRange: ClosedRange<Double>?): ClosedRange<Double>? {
            if (!Iterables.isEmpty(values)) {
                var newRange = ClosedRange.encloseAll(values)
                if (wasRange != null) {
                    newRange = wasRange.span(newRange)
                }
                return newRange
            }
            return wasRange
        }

        internal fun updateRange(range: ClosedRange<Double>?, wasRange: ClosedRange<Double>?): ClosedRange<Double>? {
            @Suppress("NAME_SHADOWING")
            var range = range
            if (range != null) {
                if (wasRange != null) {
                    range = wasRange.span(range)
                }
                return range
            }
            return wasRange
        }

        internal fun combineRanges(aesList: List<Aes<Double>>, aesthetics: Aesthetics): ClosedRange<Double>? {
            var result: ClosedRange<Double>? = null
            for (aes in aesList) {
                val range = aesthetics.range(aes)
                if (range != null) {
                    result = result?.span(range) ?: range
                }
            }
            return result
        }
    }
}