/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.common.data.SeriesUtil.isBeyondPrecision

object Transforms {
    val IDENTITY: ContinuousTransform = IdentityTransform()
    val REVERSE: ContinuousTransform = ReverseTransform()
    val SQRT: ContinuousTransform = SqrtTransform()
    val LOG10: ContinuousTransform = Log10Transform()

    fun continuousWithLimits(actual: ContinuousTransform, limits: Pair<Double?, Double?>): ContinuousTransform {
        return ContinuousTransformWithLimits(actual, limits.first, limits.second)
    }

    fun createBreaksGeneratorForTransformedDomain(
        transform: ContinuousTransform,
        labelFormatter: ((Any) -> String)? = null
    ): BreaksGenerator {
        val breaksGenerator: BreaksGenerator = when (transform.unwrap()) {
            IDENTITY -> LinearBreaksGen(labelFormatter)
            REVERSE -> LinearBreaksGen(labelFormatter)
            SQRT -> NonlinearBreaksGen(SQRT, labelFormatter)
            LOG10 -> NonlinearBreaksGen(LOG10, labelFormatter)
            else -> throw IllegalStateException("Unexpected 'transform' type: ${transform::class.simpleName}")
        }

        return BreaksGeneratorForTransformedDomain(transform, breaksGenerator)
    }

    /**
     * Use with caution!
     *
     * Do not use this method on transformed data ranges. (see 'SeriesUtil.ensureApplicableRange()')
     *
     * Only use on original data ranges.
     */
    fun ensureApplicableDomain(
        dataRange: DoubleSpan?,
        transform: ContinuousTransform
    ): DoubleSpan {
        if (dataRange == null) {
            return transform.createApplicableDomain()
        }

        val domain = transform.toApplicableDomain(dataRange)
        return when (isBeyondPrecision(domain)) {
            true -> transform.createApplicableDomain(domain.upperEnd)
            false -> domain
        }
    }

    class BreaksGeneratorForTransformedDomain(
        private val transform: ContinuousTransform,
        val breaksGenerator: BreaksGenerator
    ) : BreaksGenerator {
        override fun labelFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
            val domainBeforeTransform = ScaleUtil.applyInverseTransform(domain, transform)
            return breaksGenerator.labelFormatter(domainBeforeTransform, targetCount)
        }

        override fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
            val domainBeforeTransform = ScaleUtil.applyInverseTransform(domain, transform)
            return breaksGenerator.defaultFormatter(domainBeforeTransform, targetCount)
        }

        override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
            val domainBeforeTransform = ScaleUtil.applyInverseTransform(domain, transform)
            val scaleBreaks = breaksGenerator.generateBreaks(domainBeforeTransform, targetCount)
            val originalBreaks = scaleBreaks.domainValues
            val transformedBreaks = transform.apply(originalBreaks).map {
                it as Double // Should not contain NULLs
            }

            return ScaleBreaks(originalBreaks, transformedBreaks, scaleBreaks.labels)
        }
    }
}
