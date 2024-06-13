/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.core.plot.base.Aes

data class CustomLegendItem(
    val label: String,
    val group: String, // by which the items are combined into a legend
    val index: Int? = null,
    val aesValues: Map<Aes<*>, Any>
) {
    companion object {
        const val DEFAULT_LEGEND_GROUP_NAME = "manual"
    }
}