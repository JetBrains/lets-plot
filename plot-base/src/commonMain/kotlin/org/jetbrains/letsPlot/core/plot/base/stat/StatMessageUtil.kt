/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

internal fun emitRemovedNonFiniteValuesMessage(
    droppedCount: Int,
    totalCount: Int,
    messageConsumer: (String) -> Unit
) {
    if (droppedCount <= 0) {
        return
    }

    val rows = if (droppedCount == 1) "row" else "rows"
    messageConsumer("Removed $droppedCount $rows out of $totalCount containing non-finite values.")
}

internal fun emitRemovedBySamplingMessage(
    droppedCount: Int,
    totalCount: Int,
    samplingExpression: String,
    messageConsumer: (String) -> Unit
) {
    if (droppedCount <= 0) {
        return
    }

    val rows = if (droppedCount == 1) "row" else "rows"
    messageConsumer("Removed $droppedCount $rows out of $totalCount by $samplingExpression.")
}
