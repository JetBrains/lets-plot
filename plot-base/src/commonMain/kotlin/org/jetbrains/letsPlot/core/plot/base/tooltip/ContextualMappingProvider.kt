/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.core.plot.base.DataFrame

interface ContextualMappingProvider {
    fun createContextualMapping(dataAccess: MappedDataAccess, dataFrame: DataFrame): ContextualMapping

    companion object {
        val NONE = object : ContextualMappingProvider {
            override fun createContextualMapping(dataAccess: MappedDataAccess, dataFrame: DataFrame): ContextualMapping {
                return ContextualMapping(
                    tooltipLines = emptyList(),
                    tooltipAnchor = null,
                    tooltipMinWidth = null,
                    ignoreInvisibleTargets = false,
                    hasGeneralTooltip = false,
                    hasAxisTooltip = false,
                    isCrosshairEnabled = false,
                    tooltipTitle = null
                )
            }
        }
    }
}
