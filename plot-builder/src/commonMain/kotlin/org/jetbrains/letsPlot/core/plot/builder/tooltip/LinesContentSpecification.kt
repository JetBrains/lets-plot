/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip

import org.jetbrains.letsPlot.core.plot.builder.tooltip.data.ValueSource

data class LinesContentSpecification(
    val valueSources: List<ValueSource>,
    val linePatterns: List<LinePattern>?,
    val titleLine: LinePattern?
)