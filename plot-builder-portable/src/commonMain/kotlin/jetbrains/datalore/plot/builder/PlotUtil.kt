/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.base.gcommon.collect.Sets
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.listMapper
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.builder.assemble.GeomContextBuilder
import jetbrains.datalore.plot.common.data.SeriesUtil.isFinite
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

object PlotUtil {
    internal fun createLayerPos(layer: GeomLayer, aes: Aesthetics): PositionAdjustment {
        return layer.createPos(object : PosProviderContext {
            override val aesthetics: Aesthetics
                get() = aes

            override val groupCount: Int
                    by lazy {
                        val set = Sets.newHashSet(aes.groups())
                        set.size
                    }
        })
    }

    fun computeLayerDryRunXYRanges(
        layer: GeomLayer, aes: Aesthetics
    ): Pair<ClosedRange<Double>?, ClosedRange<Double>?> {
        val geomCtx = GeomContextBuilder().aesthetics(aes).build()

        val rangesAfterPosAdjustment =
            computeLayerDryRunXYRangesAfterPosAdjustment(layer, aes, geomCtx)
        val (rangeAfterSizeExpandFirst, rangeAfterSizeExpandSecond) =
            computeLayerDryRunXYRangesAfterSizeExpand(layer, aes, geomCtx)

        var rangeX = rangesAfterPosAdjustment.first
        if (rangeX == null) {
            rangeX = rangeAfterSizeExpandFirst
        } else if (rangeAfterSizeExpandFirst != null) {
            rangeX = rangeX.span(rangeAfterSizeExpandFirst)
        }

        var rangeY = rangesAfterPosAdjustment.second
        if (rangeY == null) {
            rangeY = rangeAfterSizeExpandSecond
        } else if (rangeAfterSizeExpandSecond != null) {
            rangeY = rangeY.span(rangeAfterSizeExpandSecond)
        }

        return Pair(rangeX, rangeY)
    }

    private fun combineRanges(aesList: List<Aes<Double>>, aesthetics: Aesthetics): ClosedRange<Double>? {
        var result: ClosedRange<Double>? = null
        for (aes in aesList) {
            val range = aesthetics.range(aes)
            if (range != null) {
                result = result?.span(range) ?: range
            }
        }
        return result
    }

    private fun computeLayerDryRunXYRangesAfterPosAdjustment(
        layer: GeomLayer, aes: Aesthetics, geomCtx: GeomContext
    ): Pair<ClosedRange<Double>?, ClosedRange<Double>?> {
        val posAesX = Iterables.toList(Aes.affectingScaleX(layer.renderedAes()))
        val posAesY = Iterables.toList(Aes.affectingScaleY(layer.renderedAes()))

        val pos = createLayerPos(layer, aes)
        if (pos.isIdentity) {
            // simplified ranges
            val rangeX = combineRanges(posAesX, aes)
            val rangeY = combineRanges(posAesY, aes)
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
                    if (isFinite(x) && isFinite(y)) {
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
            if (isFinite(loc) && isFinite(size)) {
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

    fun createLayerDryRunAesthetics(layer: GeomLayer): Aesthetics {
        val dryRunMapperByAes = HashMap<Aes<Double>, (Double?) -> Double?>()
        for (aes in layer.renderedAes()) {
            if (aes.isNumeric) {
                // safe cast: 'numeric' aes is always <Double>
                @Suppress("UNCHECKED_CAST")
                dryRunMapperByAes[aes as Aes<Double>] = Mappers.IDENTITY
            }
        }

        val mappers = prepareLayerAestheticMappers(layer, dryRunMapperByAes)
        return createLayerAesthetics(layer, mappers, emptyMap())
    }

    internal fun prepareLayerAestheticMappers(
        layer: GeomLayer,
        sharedNumericMappers: Map<Aes<Double>, (Double?) -> Double?>
    ): Map<Aes<*>, (Double?) -> Any?> {

        val mappers = HashMap<Aes<*>, (Double?) -> Any?>(sharedNumericMappers)
        for (aes in layer.renderedAes()) {
            var mapper: ((Double?) -> Any?)? = sharedNumericMappers[aes]
            if (mapper == null) {
                // positional aes share their mappers
                if (Aes.isPositionalX(aes)) {
                    mapper = sharedNumericMappers[Aes.X]
                } else if (Aes.isPositionalY(aes)) {
                    mapper = sharedNumericMappers[Aes.Y]
                }
            }
            if (mapper == null && layer.hasBinding(aes)) {
                mapper = layer.scaleMap[aes].mapper
            }

            if (mapper != null) {
                mappers[aes] = mapper
            }
        }
        return mappers
    }

    internal fun createLayerAesthetics(
        layer: GeomLayer,
        sharedMappers: Map<Aes<*>, (Double?) -> Any?>,
        overallNumericDomains: Map<Aes<Double>, ClosedRange<Double>>
    ): Aesthetics {

        val aesBuilder = AestheticsBuilder()
        aesBuilder.group(layer.group)
        for ((aes, domain) in overallNumericDomains) {
            sharedMappers[aes]?.let { mapper ->
                val range = ClosedRange(
                    mapper(domain.lowerEnd) as Double,
                    mapper(domain.upperEnd) as Double
                )
                aesBuilder.overallRange(aes, range)
            }
        }

        var hasPositionalConstants = false
        for (aes in layer.renderedAes()) {
            if (Aes.isPositional(aes) && layer.hasConstant(aes)) {
                hasPositionalConstants = true
                break
            }
        }

        val data = layer.dataFrame
        var dataPointCount: Int? = null
        for (aes in layer.renderedAes()) {
            @Suppress("UNCHECKED_CAST", "NAME_SHADOWING")
            val aes = aes as Aes<Any>

            val mapperOption = sharedMappers[aes]
            if (layer.hasConstant(aes)) {
                // Constant overrides binding
                val v = layer.getConstant(aes)
                aesBuilder.constantAes(aes, asAesValue(aes, v, mapperOption))
            } else {
                // No constant - look-up aes mapping
                if (layer.hasBinding(aes)) {
                    check(mapperOption != null) { "No scale mapper defined for aesthetic $aes" }

                    // variable at this point must be either STAT or TRANSFORM (but not ORIGIN)
                    val transformVar = DataFrameUtil.transformVarFor(aes)
                    check(data.has(transformVar)) { "Undefined var $transformVar for aesthetic $aes" }
                    val numericValues = data.getNumeric(transformVar)

                    if (dataPointCount == null) {
                        dataPointCount = numericValues.size
                    } else {
                        check(dataPointCount == numericValues.size)
                        { "" + aes + " expected data size=" + dataPointCount + " was size=" + numericValues.size }
                    }

                    if (dataPointCount == 0 && hasPositionalConstants) {
                        // put constant instead of empty list
                        aesBuilder.constantAes(aes, layer.aestheticsDefaults.defaultValue(aes))
                    } else {
                        val integerFunction = listMapper(numericValues, mapperOption)
                        aesBuilder.aes(aes, integerFunction)
                    }
                } else {
                    // apply default
                    val v = layer.getDefault(aes)
                    aesBuilder.constantAes(
                        aes,
                        asAesValue(aes, v, mapperOption)
                    )
                }
            }
        }

        if (dataPointCount != null && dataPointCount > 0) {
            aesBuilder.dataPointCount(dataPointCount)
        } else if (hasPositionalConstants) {
            // some geoms (point, abline etc.) can be plotted with only constants
            aesBuilder.dataPointCount(1)
        }

        return aesBuilder.build()
    }

    private fun <T> asAesValue(aes: Aes<*>, dataValue: T, mapperOption: ((Double?) -> T?)?): T {
        return if (aes.isNumeric && mapperOption != null) {
            mapperOption(dataValue as? Double)
                ?: throw IllegalArgumentException("Can't map $dataValue to aesthetic $aes")
        } else dataValue
    }

    /**
     * Expand X/Y-range to ensure that the data is placed some distance away from the axes.
     */
    fun rangeWithExpand(
        layer: GeomLayer,
        aes: Aes<Double>,
        range: ClosedRange<Double>?
    ): ClosedRange<Double>? {
        if (range == null) return null

        val scale = layer.scaleMap[aes]
        val mulExp = scale.multiplicativeExpand
        val addExp = scale.additiveExpand

        // Compute expands in terms of the original data.
        // Otherwise can easily run into Infinities then using 'log10' transform
        val continuousTransform: ContinuousTransform? = if (scale.isContinuousDomain) {
            scale.transform as ContinuousTransform
        } else {
            null
        }

        val lowerEndpoint = continuousTransform?.applyInverse(range.lowerEnd) ?: range.lowerEnd
        val upperEndpoint = continuousTransform?.applyInverse(range.upperEnd) ?: range.upperEnd

        val length = upperEndpoint - lowerEndpoint
        var lowerExpand = addExp + length * mulExp
        var upperExpand = lowerExpand
        if (layer.rangeIncludesZero(aes)) {
            // zero-based plots (like bar) - do not 'expand' on the zero-end
            if (lowerEndpoint == 0.0 ||
                upperEndpoint == 0.0 ||
                sign(lowerEndpoint) == sign(upperEndpoint)
            ) {
                if (lowerEndpoint >= 0) {
                    lowerExpand = 0.0
                } else {
                    upperExpand = 0.0
                }
            }
        }

        val lowerEndWithExpand = (lowerEndpoint - lowerExpand).let {
            val transformed = continuousTransform?.apply(it) ?: it
            if (transformed.isNaN()) {
                range.lowerEnd
            } else {
                transformed
            }
        }
        val upperEndWithExpand = (upperEndpoint + upperExpand).let {
            val transformed = continuousTransform?.apply(it) ?: it
            if (transformed.isNaN()) {
                range.upperEnd
            } else {
                transformed
            }
        }
        return ClosedRange(lowerEndWithExpand, upperEndWithExpand)
    }
}
