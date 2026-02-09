/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.DataType
import org.jetbrains.letsPlot.core.plot.base.FormatterUtil
import org.jetbrains.letsPlot.core.plot.base.scale.OriginalDomainBreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms.IDENTITY

/**
 * Generates breaks at fixed intervals for linear scales (identity/reverse transforms).
 * The breakWidth is in original (untransformed) units.
 */
internal class LinearFixedBreaksGen(
    private val breakWidth: Double,
    private val providedFormatter: ((Any) -> String)? = null,
    private val expFormat: ExponentFormat
) : OriginalDomainBreaksGenerator {

    override val fixedBreakWidth: Boolean = true

    override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
        val breaks = LinearBreaksHelper.generateBreaks(domain, breakWidth)

        val formatter = providedFormatter ?: FormatterUtil.byDataType(DataType.FLOATING, expFormat, tz = null)

        return ScaleBreaks.Fixed.withTransform(
            domainValues = breaks,
            transform = IDENTITY,
            formatter = formatter
        )
    }

    override fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        val helper = LinearBreaksHelper(
            domain,
            targetCount,
            providedFormatter = null,
            expFormat
        )
        return helper.formatter
    }
}
