/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.max
import kotlin.math.min

object ScaleUtil {

    // ToDo: remove
    fun labels(scale: Scale<*>): List<String> {
//        if (!scale.hasBreaks()) {
//            return emptyList()
//        }
//
//        val breaks = scale.breaks
//        if (scale.hasLabels()) {
//            val labels = scale.labels
//
//            if (breaks.size <= labels.size) {
//                return labels.subList(0, breaks.size)
//            }
//
//            val result = ArrayList<String>()
//            for (i in breaks.indices) {
//                if (labels.isEmpty()) {
//                    result.add("")
//                } else {
//                    result.add(labels[i % labels.size])
//                }
//            }
//            return result
//        }
//
//        val formatter: (Any) -> String = scale.labelFormatter ?: { v: Any -> v.toString() }
//        // generate labels
//        return breaks.map { formatter(it) }

        return scale.getScaleBreaks().labels
    }

    fun labelByBreak(scale: Scale<*>): Map<Any, String> {
//        val result = HashMap<Any, String>()
//        if (scale.hasBreaks()) {
//            val breaks = scale.breaks.iterator()
//            val labels = labels(scale).iterator()
//            while (breaks.hasNext() && labels.hasNext()) {
//                result[breaks.next()] = labels.next()
//            }
//        }
//        return result
        val scaleBreaks = scale.getScaleBreaks()
        return scaleBreaks.domainValues.zip(scaleBreaks.labels).toMap()
    }

//    fun breaksTransformed(scale: Scale<*>): List<Double> {
//        return scale.transform.apply(scale.breaks).map { it as Double }
//    }

    fun axisBreaks(scale: Scale<Double>, coord: CoordinateSystem, horizontal: Boolean): List<Double> {
//        val scaleBreaks = transformAndMap(scale.breaks, scale)
        val scaleBreaks = scale.getScaleBreaks()
        val breaksMapped = map(scaleBreaks.transformedValues, scale).filterNotNull()
        val axisBreaks = ArrayList<Double>()
        for (br in breaksMapped) {
            val mappedBrPoint = if (horizontal)
                DoubleVector(br, 0.0)
            else
                DoubleVector(0.0, br)

            val axisBrPoint = coord.toClient(mappedBrPoint)
            val axisBr = if (horizontal)
                axisBrPoint.x
            else
                axisBrPoint.y

            axisBreaks.add(axisBr)
            if (!axisBr.isFinite()) {
                throw IllegalStateException(
                    "Illegal axis '" + scale.name + "' break position " + axisBr +
                            " at index " + (axisBreaks.size - 1) +
                            "\nsource breaks    : " + scale.breaks +
                            "\ntranslated breaks: " + breaksMapped +
                            "\naxis breaks      : " + axisBreaks
                )
            }
        }
        return axisBreaks
    }

    fun map(range: ClosedRange<Double>, scale: Scale<Double>): ClosedRange<Double> {
        return MapperUtil.map(range, scale.mapper)
    }

    fun <T> map(l: List<Double?>, scale: Scale<T>): List<T?> {
        val mapper = scale.mapper
        return l.map {
            mapper(it)
        }
    }

//    fun <T> transformAndMap(l: List<*>, scale: Scale<T>): List<T?> {
//        val cleaned = cleanUpTransformSource(l, scale)
//        val transformed = scale.transform.apply(cleaned)
//        return map(transformed, scale)
//    }

//    fun cleanUpTransformSource(source: List<*>, scale: Scale<*>): List<Any?> {
//        @Suppress("NAME_SHADOWING")
//        var source: List<Any?> = source
//
//        // Replace values outside 'scale limits' with null-s.
//        if (scale.hasDomainLimits()) {
//            source = source.map { if (it == null || scale.isInDomainLimits(it)) it else null }
//        }
//
//        // Replace values outside of domain of 'continuous transform' with null-s.
//        if (scale.transform is ContinuousTransform) {
//            val continuousTransform = scale.transform as ContinuousTransform
//            if (continuousTransform.hasDomainLimits()) {
//                source = source.map { if (continuousTransform.isInDomain(it as Double?)) it else null }
//            }
//        }
//
//        return source
//    }


    fun inverseTransformToContinuousDomain(l: List<Double?>, scale: Scale<*>): List<Double?> {
        check(scale.isContinuousDomain) { "Not continuous numeric domain: $scale" }
        return (scale.transform as ContinuousTransform).applyInverse(l)
    }

    fun inverseTransform(l: List<Double?>, scale: Scale<*>): List<*> {
        val transform = scale.transform
        return if (transform is ContinuousTransform) {
            transform.applyInverse(l)
        } else {
            l.map { transform.applyInverse(it) }
        }
    }

    fun transformedDefinedLimits(scale: Scale<*>): Pair<Double, Double> {
        val (lower, upper) = scale.domainLimits
        val transform = scale.transform as ContinuousTransform
        val (transformedLower, transformedUpper) = Pair(
            if (transform.isInDomain(lower)) transform.apply(lower)!! else Double.NaN,
            if (transform.isInDomain(upper)) transform.apply(upper)!! else Double.NaN
        )

        return if (SeriesUtil.allFinite(transformedLower, transformedUpper)) {
            Pair(
                min(transformedLower, transformedUpper),
                max(transformedLower, transformedUpper)
            )
        } else {
            Pair(transformedLower, transformedUpper)
        }
    }
}
