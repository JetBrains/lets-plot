/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil

internal object DataUtil {
    fun addRow(df: DataFrame, getValue: (DataFrame.Variable) -> Any?, position: Int? = null): DataFrame {
        val builder = DataFrame.Builder()
        df.variables().forEach { variable ->
            val values = df[variable].toMutableList()
            if (position == null) {
                values.add(getValue(variable))
            } else {
                values.add(position, getValue(variable))
            }
            builder.put(variable, values)
        }
        return builder.build()
    }

    fun setColumn(df: DataFrame, variable: DataFrame.Variable, values: List<Any?>): DataFrame {
        val builder = DataFrame.Builder()
        df.variables().forEach { v ->
            if (v.name != variable.name) {
                builder.put(v, df[v])
            }
        }
        builder.put(variable, values)
        return builder.build()
    }

    fun groupBy(df: DataFrame, group: String?): Map<Any, DataFrame> {
        val groupVar = group?.let { DataFrameUtil.findVariableOrNull(df, it) } ?: return mapOf(0 to df)
        val groupValues = df.distinctValues(groupVar)
        return groupValues.associate { groupValue ->
            val indices = df[groupVar].withIndex().map { (i, v) -> Pair(i, v) }.filter { (_, v) -> v == groupValue }.unzip().first
            groupValue to df.slice(indices)
        }
    }

    fun concat(dataframes: List<DataFrame>, emptyDataframe: DataFrame): DataFrame {
        if (dataframes.isEmpty()) return emptyDataframe
        val builder = DataFrame.Builder()
        dataframes.first().variables().forEach { variable ->
            builder.put(variable, dataframes.map { df -> df[variable] }.flatten())
        }
        return builder.build()
    }
}