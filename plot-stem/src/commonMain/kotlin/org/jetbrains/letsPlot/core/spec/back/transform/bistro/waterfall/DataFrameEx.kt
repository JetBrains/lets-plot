/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.commons.intern.indicesOf
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil

fun DataFrame.addRow(getValue: (DataFrame.Variable) -> Any?, position: Int? = null): DataFrame {
    val builder = DataFrame.Builder()
    variables().forEach { variable ->
        val values = get(variable).toMutableList()
        if (position == null) {
            values.add(getValue(variable))
        } else {
            values.add(position, getValue(variable))
        }
        builder.put(variable, values)
    }
    return builder.build()
}

fun DataFrame.setColumn(variable: DataFrame.Variable, values: List<Any?>): DataFrame {
    return builder().let { builder ->
        builder.put(DataFrameUtil.findVariableOrNull(this, variable.name) ?: variable, values)
        builder.build()
    }
}

fun DataFrame.groupBy(group: String?): Map<Any, DataFrame> {
    val groupVar = group?.let { DataFrameUtil.findVariableOrNull(this, it) } ?: return mapOf(0 to this)
    val groupValues = distinctValues(groupVar)
    return groupValues.associate { groupValue ->
        groupValue to slice(get(groupVar).indicesOf { it == groupValue })
    }
}

fun DataFrame.replace(
    key: DataFrame.Variable,
    filter: (Any?) -> Boolean,
    replace: (DataFrame.Variable) -> (Any?) -> Any?
): DataFrame {
    val indices = get(key).indicesOf { filter(it) }
    val builder = DataFrame.Builder()
    variables().forEach { variable ->
        get(variable).withIndex().map { (i, v) ->
            if (i in indices) replace(variable)(v) else v
        }.also { values ->
            builder.put(variable, values)
        }
    }
    return builder.build()
}