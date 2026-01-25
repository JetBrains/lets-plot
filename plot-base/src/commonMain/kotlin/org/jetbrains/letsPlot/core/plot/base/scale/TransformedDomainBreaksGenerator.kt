/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.LinearBreaksGen
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.LinearFixedBreaksGen
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.NonlinearBreaksGen
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.NonlinearFixedBreaksGen
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms

/**
 * Wraps an "original domain" breaks generator and adapts it to work with "transformed domain".
 */
class TransformedDomainBreaksGenerator(
    private val transform: ContinuousTransform,
    val originalDomainBreaksGen: OriginalDomainBreaksGenerator
) {

    val fixedBreakWidth: Boolean
        get() = originalDomainBreaksGen.fixedBreakWidth

    fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
        val domainBeforeTransform = ScaleUtil.applyInverseTransform(domain, transform)
        val breaksNoTransform = originalDomainBreaksGen.generateBreaks(domainBeforeTransform, targetCount)
        return breaksNoTransform.withTransform(transform)
    }

    fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        val domainBeforeTransform = ScaleUtil.applyInverseTransform(domain, transform)
        return originalDomainBreaksGen.defaultFormatter(domainBeforeTransform, targetCount)
    }

    companion object {
        fun forTransform(
            transform: ContinuousTransform,
            providedFormatter: ((Any) -> String)? = null,
            expFormat: StringFormat.ExponentFormat
        ): TransformedDomainBreaksGenerator {
            val breaksGenerator: OriginalDomainBreaksGenerator = when (transform.unwrap()) {
                Transforms.IDENTITY -> LinearBreaksGen(providedFormatter, expFormat)
                Transforms.REVERSE -> LinearBreaksGen(providedFormatter, expFormat)
                Transforms.SQRT -> NonlinearBreaksGen(Transforms.SQRT, providedFormatter, expFormat)
                Transforms.LOG10 -> NonlinearBreaksGen(Transforms.LOG10, providedFormatter, expFormat)
                Transforms.LOG2 -> NonlinearBreaksGen(Transforms.LOG2, providedFormatter, expFormat)
                Transforms.SYMLOG -> NonlinearBreaksGen(Transforms.SYMLOG, providedFormatter, expFormat)
                else -> throw IllegalStateException("Unexpected 'transform' type: ${transform::class.simpleName}")
            }

            return TransformedDomainBreaksGenerator(transform, breaksGenerator)
        }

        fun forTransformWithFixedBreakWidth(
            transform: ContinuousTransform,
            breakWidth: Double,
            providedFormatter: ((Any) -> String)? = null,
            expFormat: StringFormat.ExponentFormat
        ): TransformedDomainBreaksGenerator {
            val breaksGenerator: OriginalDomainBreaksGenerator = when (transform.unwrap()) {
                Transforms.IDENTITY -> LinearFixedBreaksGen(breakWidth, providedFormatter, expFormat)
                Transforms.REVERSE -> LinearFixedBreaksGen(breakWidth, providedFormatter, expFormat)
                Transforms.SQRT -> NonlinearFixedBreaksGen(breakWidth, Transforms.SQRT, providedFormatter, expFormat)
                Transforms.LOG10 -> NonlinearFixedBreaksGen(breakWidth, Transforms.LOG10, providedFormatter, expFormat)
                Transforms.LOG2 -> NonlinearFixedBreaksGen(breakWidth, Transforms.LOG2, providedFormatter, expFormat)
                Transforms.SYMLOG -> NonlinearFixedBreaksGen(breakWidth, Transforms.SYMLOG, providedFormatter, expFormat)
                else -> throw IllegalStateException("Unexpected 'transform' type: ${transform::class.simpleName}")
            }

            return TransformedDomainBreaksGenerator(transform, breaksGenerator)
        }
    }
}