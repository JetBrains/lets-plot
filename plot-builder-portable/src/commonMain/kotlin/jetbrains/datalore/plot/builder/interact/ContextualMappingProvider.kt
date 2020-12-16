/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.builder.tooltip.TooltipSpecification.TooltipProperties

interface ContextualMappingProvider {
    fun createContextualMapping(
        dataAccess: MappedDataAccess,
        dataFrame: DataFrame,
        tooltipProperties: TooltipProperties
    ): ContextualMapping

    companion object {
        val NONE = object : ContextualMappingProvider {
            override fun createContextualMapping(
                dataAccess: MappedDataAccess,
                dataFrame: DataFrame,
                tooltipProperties: TooltipProperties
            ): ContextualMapping {
                return ContextualMapping(
                    tooltipLines = emptyList(),
                    tooltipAnchor = null,
                    tooltipMinWidth = null,
                    tooltipColor = null,
                    ignoreInvisibleTargets = false
                )
            }
        }
    }
}
