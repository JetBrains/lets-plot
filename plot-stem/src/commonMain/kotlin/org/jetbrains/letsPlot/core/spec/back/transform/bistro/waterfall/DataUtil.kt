/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil

internal object DataUtil {
    fun addRow(df: DataFrame, getValue: (DataFrame.Variable) -> Any?): DataFrame {
        val builder = DataFrame.Builder()
        df.variables().map { variable ->
            builder.put(variable, df[variable] + listOf(getValue(variable)))
        }
        return builder.build()
    }

    fun addColumn(df: DataFrame, variable: DataFrame.Variable, values: List<Any?>): DataFrame {
        val builder = DataFrame.Builder()
        df.variables().map { variable ->
            builder.put(variable, df[variable])
        }
        builder.put(variable, values)
        return builder.build()
    }

    fun groupBy(df: DataFrame, group: String?): List<DataFrame> {
        val groupVar = group?.let { DataFrameUtil.findVariableOrNull(df, it) } ?: return listOf(df)
        val groupValues = df.distinctValues(groupVar)
        return groupValues.map { groupValue ->
            val indices = df[groupVar].withIndex().map { (i, v) -> Pair(i, v) }.filter { (_, v) -> v == groupValue }.unzip().first
            df.slice(indices)
        }
    }

    fun concat(dataframes: List<DataFrame>, emptyDataframe: DataFrame): DataFrame {
        if (dataframes.isEmpty()) return emptyDataframe
        val builder = DataFrame.Builder()
        dataframes.first().variables().map { variable ->
            builder.put(variable, dataframes.map { df -> df[variable] }.flatten())
        }
        return builder.build()
    }
}