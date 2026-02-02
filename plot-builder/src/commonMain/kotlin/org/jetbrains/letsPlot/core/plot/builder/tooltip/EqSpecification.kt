/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip

data class EqSpecification(
    val lhs: String?,
    val rhs: String?,
    val formats: List<String>,
    val threshold: Double?,
)