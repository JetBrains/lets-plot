/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.data

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DataFrame.Variable
import org.jetbrains.letsPlot.core.plot.builder.data.DataProcessing.findOptionalVariable
import org.jetbrains.letsPlot.core.plot.builder.data.GroupMapperHelper.SINGLE_GROUP

class GroupingContext constructor(
    private val data: DataFrame,
    defaultGroupingVariables: List<Variable>,
    explicitGroupingVarName: String?,
    private val expectMultiple: Boolean,
) {

    internal val optionalGroupingVar: Variable? = findOptionalVariable(data, explicitGroupingVarName)
    private val groupingVariables: List<Variable> = when (optionalGroupingVar) {
        null -> defaultGroupingVariables
        else -> {
            // Explicit group aesthetic should override default grouping.
            listOf(optionalGroupingVar)
        }
    }

    val groupMapper: (Int) -> Int by lazy {
        computeGroups()
    }

    private fun computeGroups(): (Int) -> Int {
        return if (!expectMultiple) {
            SINGLE_GROUP
        } else {
            GroupMapperHelper.firstOptionGroupMapperOrNull(data)
                ?: DataProcessing.computeGroups(
                    data,
                    groupingVariables
                )
        }
    }
}
