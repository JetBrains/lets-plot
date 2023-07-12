/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale

import org.jetbrains.letsPlot.commons.interval.DoubleSpan

interface BreaksGenerator {
    fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks

    /**
     * Formatting arbitrary datapoints according to user-defined format or default formatter.
     */
    fun labelFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String

    /**
     * Formatting arbitrary datapoints excluding user-defined options; the formatter is used for labels and tooltips.
     */
    fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String
}
