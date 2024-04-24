/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

data class LegendItem(
    val key: String,
    val label: String,
    val index: Int? = null
) {
    companion object {
        const val DEFAULT_CUSTOM_LEGEND_KEY = "custom_key"
    }
}