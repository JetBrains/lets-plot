/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.interact.ContextualMapping
import org.jetbrains.letsPlot.core.plot.base.interact.MappedDataAccess

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
