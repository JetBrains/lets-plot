/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.scale.BreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.LinearBreaksHelper

internal class LinearBreaksGen(
    private val providedFormatter: ((Any) -> String)? = null,
    private val superscriptExponent: Boolean,
) : BreaksGenerator {

    override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
        val helper = LinearBreaksHelper(
            domain.lowerEnd,
            domain.upperEnd,
            targetCount,
            superscriptExponent
        )
        val formatter = providedFormatter ?: helper.formatter
        return ScaleBreaks(
            domainValues = helper.breaks,
            transformedValues = helper.breaks,
            formatter = formatter
        )
    }

    override fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        val helper = LinearBreaksHelper(
            domain.lowerEnd,
            domain.upperEnd,
            targetCount,
            superscriptExponent
        )
        return helper.formatter
    }
}
