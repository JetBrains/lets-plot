/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

abstract class BreaksHelperBase(
    start: Double,
    end: Double,
    targetCount: Int,
) {
    abstract val breaks: List<Double>

    protected val normalStart: Double
    protected val normalEnd: Double
    protected val span: Double
    protected val targetStep: Double
    protected val isReversed: Boolean

    init {
        require(start.isFinite()) { "range start $start" }
        require(end.isFinite()) { "range end $end" }
        require(targetCount > 0) { "'count' must be positive: $targetCount" }
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
