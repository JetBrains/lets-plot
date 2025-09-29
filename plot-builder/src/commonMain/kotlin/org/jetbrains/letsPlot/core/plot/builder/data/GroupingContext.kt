/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.data

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DataFrame.Variable
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.data.GroupMapperHelper.SINGLE_GROUP

abstract class GroupingContext {

    abstract val groupingVariables: List<Variable>
    val groupMapper: (Int) -> Int by lazy {
        computeGroups()
    }

    abstract fun computeGroups(): (Int) -> Int

    companion object {

        fun singleGroup(): GroupingContext {
            return object : GroupingContext() {
                override val groupingVariables: List<Variable> = emptyList()
                override fun computeGroups(): (Int) -> Int = SINGLE_GROUP
            }
        }

        fun create(
            data: DataFrame,
            explicitGroupingVarNames: List<String>?,
            varBindings: List<VarBinding>,
            pathIdVarName: String?,
        ): GroupingContext {
            val groupingVariables = if (explicitGroupingVarNames == null) {
                // Default grouping configuration
                DataProcessing.defaultGroupingVariables(
                    data,
                    varBindings,
                    pathIdVarName
                )
            } else if (explicitGroupingVarNames.isEmpty()) {
                // Cancel all grouping
                emptyList<Variable>()
            } else {
                // Explicit 'group' aesthetic mapping overrides default grouping.
                explicitGroupingVarNames.map { name ->
                    DataFrameUtil.findVariableOrFail(data, name)
                }
            }

            return createIntern(data, groupingVariables)
        }

        fun create(
            data: DataFrame,
            groupingVarNames: List<String>,
        ): GroupingContext {
            val groupingVariables = groupingVarNames.map { name ->
                DataFrameUtil.findVariableOrFail(data, name)
            }
            return createIntern(data, groupingVariables)
        }

        private fun createIntern(
            data: DataFrame,
            groupingVariables: List<Variable>,
        ): GroupingContext {
            return object : GroupingContext() {
                override val groupingVariables: List<Variable> = groupingVariables

                override fun computeGroups(): (Int) -> Int {
                    // TODO: try doing `GroupMapperHelper.firstOptionGroupMapperOrNull(data)` earlier.
                    return GroupMapperHelper.firstOptionGroupMapperOrNull(data)
                        ?: DataProcessing.computeGroups(
                            data,
                            groupingVariables
                        )
                }
            }
        }
    }
}
