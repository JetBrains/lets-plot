/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.common.data.SeriesUtil

object Transforms {
    val IDENTITY: ContinuousTransform = IdentityTransform()
    val REVERSE: ContinuousTransform = ReverseTransform()
    val SQRT: ContinuousTransform = SqrtTransform()
    val LOG10: ContinuousTransform = Log10Transform()

    fun createBreaksGeneratorForTransformedDomain(
        transform: ContinuousTransform,
        labelFormatter: ((Any) -> String)? = null
    ): BreaksGenerator {
        val breaksGenerator: BreaksGenerator = when (transform) {
            IDENTITY -> LinearBreaksGen(labelFormatter)
            REVERSE -> LinearBreaksGen(labelFormatter)
            SQRT -> NonlinearBreaksGen(SQRT, labelFormatter)
            LOG10 -> NonlinearBreaksGen(LOG10, labelFormatter)
            else -> throw IllegalStateException("Unexpected 'transform' type: ${transform::class.simpleName}")
        }

        return BreaksGeneratorForTransformedDomain(transform, breaksGenerator)
    }

    fun ensureApplicableDomain(
        dataRange: ClosedRange<Double>?,
        transform: ContinuousTransform
    ): ClosedRange<Double> {
        if (dataRange == null) {
            return transform.createApplicableDomain()
        }

        val domain = transform.toApplicableDomain(dataRange)
        return when {
            SeriesUtil.isSubTiny(domain) ->
                transform.createApplicableDomain(domain.upperEnd)
            else ->
                domain
        }
    }


    class BreaksGeneratorForTransformedDomain(
        private val transform: ContinuousTransform,
        val breaksGenerator: BreaksGenerator
    ) : BreaksGenerator {
        override fun labelFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String {
            val domainBeforeTransform = MapperUtil.map(domain, transform::applyInverse)
            return breaksGenerator.labelFormatter(domainBeforeTransform, targetCount)
        }

        override fun defaultFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String {
            val domainBeforeTransform = MapperUtil.map(domain, transform::applyInverse)
            return breaksGenerator.defaultFormatter(domainBeforeTransform, targetCount)
        }

        override fun generateBreaks(domain: ClosedRange<Double>, targetCount: Int): ScaleBreaks {
            val domainBeforeTransform = MapperUtil.map(domain, transform::applyInverse)
            val scaleBreaks = breaksGenerator.generateBreaks(domainBeforeTransform, targetCount)
            val originalBreaks = scaleBreaks.domainValues
            val transformedBreaks = transform.apply(originalBreaks).map {
                it as Double // Should not contain NULLs
            }

            return ScaleBreaks(originalBreaks, transformedBreaks, scaleBreaks.labels)
        }
    }
}
