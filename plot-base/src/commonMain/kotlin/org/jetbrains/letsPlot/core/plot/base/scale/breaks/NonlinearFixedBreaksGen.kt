/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.scale.OriginalDomainBreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleUtil
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms

/**
 * Generates breaks at fixed intervals for nonlinear scales (log10, log2, sqrt, symlog).
 * The breakWidth is in transformed units (e.g., for log10: breakWidth=1 means every power of 10).
 */
internal class NonlinearFixedBreaksGen(
    private val breakWidth: Double,
    private val transform: ContinuousTransform,
    private val providedFormatter: ((Any) -> String)? = null,
    private val expFormat: ExponentFormat
) : OriginalDomainBreaksGenerator {

    override val fixedBreakWidth: Boolean = true

    override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
        // Transform domain to transformed space
        val transformedDomain = ScaleUtil.applyTransform(domain, transform)

        // Generate breaks at fixed intervals in transformed space
        val transformedBreaks = LinearBreaksHelper.generateBreaks(transformedDomain, breakWidth)

        // Transform back to original space
        val breaks = transform.applyInverse(transformedBreaks).filterNotNull()

        val formatter = providedFormatter ?: NonlinearBreaksHelper.createMultiFormatter(breaks, expFormat)

        return ScaleBreaks.Fixed.withTransform(
            domainValues = breaks,
            transform = Transforms.IDENTITY,
            formatter = formatter
        )
    }

    override fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        val helper = NonlinearBreaksHelper(
            domain,
            targetCount,
            providedFormatter = null,
            expFormat,
            transform,
            niceLogBreaks = false
        )
        return helper.formatter
    }
}
