/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange

interface BreaksGenerator {
    fun generateBreaks(domain: ClosedRange<Double>, targetCount: Int): ScaleBreaks

    /**
     * Formatting arbitrary datapoints according to user-defined format or default formatter.
     */
    fun labelFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String

    /**
     * Formatting arbitrary datapoints excluding user-defined options; the formatter is used for labels and tooltips.
     */
    fun defaultFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String
}
