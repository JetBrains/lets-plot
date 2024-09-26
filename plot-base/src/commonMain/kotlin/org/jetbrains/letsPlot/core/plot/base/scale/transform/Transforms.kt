/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentFormat
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.isBeyondPrecision
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.scale.BreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleUtil
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.LinearBreaksGen
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.NonlinearBreaksGen

object Transforms {
    val IDENTITY: ContinuousTransform = IdentityTransform()
    val REVERSE: ContinuousTransform = ReverseTransform()
    val SQRT: ContinuousTransform = SqrtTransform()
    val LOG10: ContinuousTransform = Log10Transform()
    val LOG2: ContinuousTransform = Log2Transform()
    val SYMLOG: ContinuousTransform = SymlogTransform()

    fun continuousWithLimits(actual: ContinuousTransform, limits: Pair<Double?, Double?>): ContinuousTransform {
        return ContinuousTransformWithLimits(actual, limits.first, limits.second)
    }

    fun createBreaksGeneratorForTransformedDomain(
        transform: ContinuousTransform,
        providedFormatter: ((Any) -> String)? = null,
        exponentFormat: ExponentFormat,
        minExponent: Int,
        maxExponent: Int?
    ): BreaksGenerator {
        val breaksGenerator: BreaksGenerator = when (transform.unwrap()) {
            IDENTITY -> LinearBreaksGen(providedFormatter, exponentFormat, minExponent, maxExponent)
            REVERSE -> LinearBreaksGen(providedFormatter, exponentFormat, minExponent, maxExponent)
            SQRT -> NonlinearBreaksGen(SQRT, providedFormatter, exponentFormat, minExponent, maxExponent)
            LOG10 -> NonlinearBreaksGen(LOG10, providedFormatter, exponentFormat, minExponent, maxExponent)
            LOG2 -> NonlinearBreaksGen(LOG2, providedFormatter, exponentFormat, minExponent, maxExponent)
            SYMLOG -> NonlinearBreaksGen(SYMLOG, providedFormatter, exponentFormat, minExponent, maxExponent)
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
        override fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
            val domainBeforeTransform = ScaleUtil.applyInverseTransform(domain, transform)
            return breaksGenerator.defaultFormatter(domainBeforeTransform, targetCount)
        }

        override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
            val domainBeforeTransform = ScaleUtil.applyInverseTransform(domain, transform)
            val breaksNoTransform = breaksGenerator.generateBreaks(domainBeforeTransform, targetCount)
            return breaksNoTransform.withTransform(transform)
        }
    }
}
