/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.common.data.SeriesUtil

class PlotFacets(val xVar: String?, val yVar: String?, val xLevels: List<*>?, val yLevels: List<*>?) {

    val isDefined: Boolean
        get() = xVar != null || yVar != null

    fun dataSubset(data: DataFrame, xLevel: Any?, yLevel: Any?): DataFrame {
        if (xLevel == null && yLevel == null) {
            return data
        }

        val matchingIndices: MutableList<Int>
        if (xLevel == null) {                                 // all 'x'
            val `var` = DataFrameUtil.findVariableOrFail(data, yVar!!)
            val list = data[`var`]
            matchingIndices = SeriesUtil.matchingIndices(list, yLevel!!)
        } else if (yLevel == null) {                          // all 'y'
            val `var` = DataFrameUtil.findVariableOrFail(data, xVar!!)
            val list = data[`var`]
            matchingIndices = SeriesUtil.matchingIndices(list, xLevel)
        } else {
            val varX = DataFrameUtil.findVariableOrFail(data, xVar!!)
            val varY = DataFrameUtil.findVariableOrFail(data, yVar!!)
            matchingIndices = SeriesUtil.matchingIndices(data[varX], xLevel)
            val matchingY = SeriesUtil.matchingIndices(data[varY], yLevel)
            // intersection
            matchingIndices.retainAll(HashSet(matchingY))
        }

        // build the data subset
        val dfBuilder = DataFrame.Builder()
        val variables = data.variables()
        for (variable in variables) {
            val source = data[variable]
            val target = SeriesUtil.pickAtIndices(source, matchingIndices)
            dfBuilder.put(variable, target)
        }

        return dfBuilder.build()
    }

    companion object {
        fun undefined(): PlotFacets {
            return PlotFacets(null, null, null, null)
        }
    }
}
