/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.builder.data.DataProcessing.findOptionalVariable
import jetbrains.datalore.plot.builder.data.GroupMapperHelper.SINGLE_GROUP

class GroupingContext(
    private val data: DataFrame,
    defaultGroupingVariables: List<Variable>,
    explicitGroupingVarName: String?,
    private val expectMultiple: Boolean,
) {

    internal val optionalGroupingVar: Variable? = findOptionalVariable(data, explicitGroupingVarName)
    private val groupingVariables: List<Variable> = when (optionalGroupingVar) {
        null -> defaultGroupingVariables
        else -> {
            // The explicit grouping var was 1-st in list before so we just keep this invariant.
            (linkedSetOf(optionalGroupingVar) + defaultGroupingVariables).toList()
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
