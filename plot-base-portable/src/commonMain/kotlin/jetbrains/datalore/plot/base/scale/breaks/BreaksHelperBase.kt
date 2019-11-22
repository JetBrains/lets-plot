/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.breaks

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument

abstract class BreaksHelperBase protected constructor(
    start: Double,
    end: Double,
    targetCount: Int
) {
    abstract val breaks: List<Double>
    abstract val labelFormatter: (Any) -> String

    protected val normalStart: Double
    protected val normalEnd: Double
    protected val span: Double
    protected val targetStep: Double
    protected val isReversed: Boolean

    init {
        checkArgument(start.isFinite(), "range start $start")
        checkArgument(end.isFinite(), "range end $end")
        checkArgument(targetCount > 0, "'count' must be positive: $targetCount")
        var span = end - start
        var reversed = false
        if (span < 0) {
            span = -span
            reversed = true
        }

        this.span = span
        targetStep = this.span / targetCount
        isReversed = reversed
        normalStart = if (reversed) end else start
        normalEnd = if (reversed) start else end
    }
}
