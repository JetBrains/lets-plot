/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.listMapper
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.builder.assemble.PosProvider
import kotlin.math.sign

object PlotUtil {
    internal fun createPositionAdjustment(posProvider: PosProvider, aes: Aesthetics): PositionAdjustment {
        return posProvider.createPos(object : PosProviderContext {
            override val aesthetics: Aesthetics
                get() = aes

            override val groupCount: Int
                    by lazy {
                        val set = aes.groups().toSet()
                        set.size
                    }
        })
    }

    internal fun prepareLayerAestheticMappers(
        layer: GeomLayer,
        xAesMapper: ScaleMapper<Double>,
        yAesMapper: ScaleMapper<Double>,
    ): Map<Aes<*>, ScaleMapper<*>> {

        val yOrientation = layer.isYOrientation
        val mappers = HashMap<Aes<*>, ScaleMapper<*>>()
        val renderedAes = layer.renderedAes() + listOf(Aes.X, Aes.Y)
        for (aes in renderedAes) {
            var mapper: ScaleMapper<*>? = when {
                aes == Aes.SLOPE -> Mappers.mul(yAesMapper(1.0)!! / xAesMapper(1.0)!!)
                // positional aes share their mappers
                aes == Aes.X -> xAesMapper
                aes == Aes.Y -> yAesMapper
                Aes.isPositionalX(aes) -> if (yOrientation) yAesMapper else xAesMapper
                Aes.isPositionalY(aes) -> if (yOrientation) xAesMapper else yAesMapper
                layer.hasBinding(aes) -> layer.scaleMapppersNP.getValue(aes)
                else -> null  // rendered but has no binding - just ignore.
            }

            mapper?.let {
                mappers[aes] = it
            }
        }
        return mappers
    }

    internal fun createLayerAesthetics(
        layer: GeomLayer,
        aesList: List<Aes<*>>,
        mapperByAes: Map<Aes<*>, ScaleMapper<*>>,
    ): Aesthetics {

        val aesBuilder = AestheticsBuilder()
        aesBuilder.group(layer.group)
            .colorAes(layer.colorByAes)
            .fillAes(layer.fillByAes)

        var hasPositionalConstants = false
        for (aes in aesList) {
            if (Aes.isPositional(aes) && layer.hasConstant(aes)) {
                hasPositionalConstants = true
                break
            }
        }

        val data = layer.dataFrame
        var dataPointCount: Int? = null
        for (aes in aesList) {
            @Suppress("UNCHECKED_CAST", "NAME_SHADOWING")
            val aes = aes as Aes<Any>

            val mapperOption = when {
                Aes.isPositional(aes) -> Mappers.IDENTITY
                else -> mapperByAes[aes]
            }

            if (layer.hasConstant(aes)) {
                // Constant overrides binding
                val v = layer.getConstant(aes)
                val t = transformIfContinuous(scale(aes, layer))
                aesBuilder.constantAes(aes, constantToAesValue(aes, v, t, mapperOption))
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
                    val t = transformIfContinuous(scale(aes, layer))
                    aesBuilder.constantAes(
                        aes,
                        constantToAesValue(aes, v, t, mapperOption)
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

    private fun constantToAesValue(
        aes: Aes<*>,
        v: Any?,
        continuousTransform: ContinuousTransform?,
        mapperOption: ScaleMapper<*>?
    ): Any? {

        return if (aes.isNumeric) {
            // Constants for numerin Aes : x, y, size etc.
            // should be transformed before further mapping is applied.
            val transformed = if (continuousTransform != null) {
                when (continuousTransform.isInDomain(v as Double)) {
                    true -> continuousTransform.apply(v)
                    false -> null
                }
            } else {
                v as? Double   // Aes like 'width', 'height' not expected to have a transform.
            }

            mapperOption?.invoke(transformed) ?: transformed
        } else {
            v
        }
    }

    /**
     * Expand X/Y-range to ensure that the data is placed some distance away from the axes.
     */
    internal fun rangeWithExpand(
        range: DoubleSpan?,
        scale: Scale,
        includeZero: Boolean
    ): DoubleSpan? {
        if (range == null) return null

        val mulExp = scale.multiplicativeExpand
        val addExp = scale.additiveExpand

        // Compute expands in terms of the original data.
        // Otherwise, can easily run into Infinities then using 'log10' transform
        val continuousTransform: ContinuousTransform? = transformIfContinuous(scale)

        // Inverse transform ends and make sure that lowe <= upper
        val domain = DoubleSpan(
            continuousTransform?.applyInverse(range.lowerEnd) ?: range.lowerEnd,
            continuousTransform?.applyInverse(range.upperEnd) ?: range.upperEnd
        )
        val lowerEndpoint = domain.lowerEnd
        val upperEndpoint = domain.upperEnd

        val length = upperEndpoint - lowerEndpoint
        var lowerExpand = addExp + length * mulExp
        var upperExpand = lowerExpand
        if (includeZero) {
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
        return DoubleSpan(lowerEndWithExpand, upperEndWithExpand)
    }

    private fun transformIfContinuous(scale: Scale?): ContinuousTransform? {
        if (scale == null) return null
        return if (scale.isContinuousDomain) {
            scale.transform as ContinuousTransform
        } else {
            null
        }
    }

    private fun scale(aes: Aes<*>, layer: GeomLayer): Scale? {
        @Suppress("NAME_SHADOWING")
        val aes = when {
            Aes.isPositionalXY(aes) -> Aes.toAxisAes(aes, layer.isYOrientation)
            else -> aes
        }
        return if (layer.scaleMap.containsKey(aes)) {
            layer.scaleMap[aes]
        } else {
            // Aes like 'width', 'height' do not have scale.
            null
        }
    }

    object DemoAndTest {
        fun layerAestheticsWithoutLayout(layer: GeomLayer): Aesthetics {
            val mappers = prepareLayerAestheticMappers(
                layer,
                xAesMapper = Mappers.IDENTITY,
                yAesMapper = Mappers.IDENTITY
            )
            return createLayerAesthetics(layer, layer.renderedAes(), mappers)
        }
    }
}
