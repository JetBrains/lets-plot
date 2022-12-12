/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.TransformVar
import kotlin.test.Test

class Count2dStatTest : BaseStatTest() {

    @Test
    fun emptyDataFrame() {
        testEmptyDataFrame(Stats.count2d())
    }

    @Test
    fun oneElementDataFrame() {
        val df = dataFrame(
            mapOf(
                TransformVar.X to listOf(0.0)
            )
        )

        val stat = Stats.count2d()
        val statDf = stat.normalize(stat.apply(df, statContext(df)))

        checkStatVarValues(statDf, Stats.X, listOf(0.0))
        checkStatVarValues(statDf, Stats.Y, listOf(0.0))
        checkStatVarValues(statDf, Stats.COUNT, listOf(1.0))
        checkStatVarValues(statDf, Stats.SUM, listOf(1.0))
        checkStatVarValues(statDf, Stats.PROP, listOf(1.0))
        checkStatVarValues(statDf, Stats.PROPPCT, listOf(100.0))
    }

    @Test
    fun twoElementsInDataFrame() {
        val df = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to listOf(0.0, 0.0),
                TransformVar.WEIGHT.name to listOf(1.0, 3.0)
            )
        )

        val stat = Stats.count2d()
        val statDf = stat.normalize(stat.apply(df, statContext(df)))

        checkStatVarValues(statDf, Stats.X, listOf(0.0))
        checkStatVarValues(statDf, Stats.Y, listOf(0.0))
        checkStatVarValues(statDf, Stats.COUNT, listOf(4.0))
        checkStatVarValues(statDf, Stats.SUM, listOf(4.0))
        checkStatVarValues(statDf, Stats.PROP, listOf(1.0))
        checkStatVarValues(statDf, Stats.PROPPCT, listOf(100.0))
    }

    @Test
    fun twoGroups() {
        val df0 = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to listOf(0.0),
                TransformVar.WEIGHT.name to listOf(1.0)
            )
        )
        val df1 = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to listOf(0.0, 0.0),
                TransformVar.WEIGHT.name to listOf(1.0, 2.0)
            )
        )

        val stat = Stats.count2d()
        val afterStat0 = stat.apply(df0, statContext(df0))
        val afterStat1 = stat.apply(df1, statContext(df1))

        fun union(df0: DataFrame, df1: DataFrame): DataFrame {
            val map0 = DataFrameUtil.toMap(df0)
            val map1 = DataFrameUtil.toMap(df1)
            val merged = map0.map { (key, value) ->
                val list = value.toMutableList()
                if (key in map1) {
                    list.addAll(map1[key]!!)
                }
                key to list.toList()
            }.toMap()
            return DataFrameUtil.fromMap(merged)
        }

        val statDf = stat.normalize(dataAfterStat = union(afterStat0, afterStat1))

        checkStatVarValues(statDf, Stats.X, listOf(0.0, 0.0))
        checkStatVarValues(statDf, Stats.Y, listOf(0.0, 0.0))
        checkStatVarValues(statDf, Stats.COUNT, listOf(1.0, 3.0))
        checkStatVarValues(statDf, Stats.SUM, listOf(4.0, 4.0))
        checkStatVarValues(statDf, Stats.PROP, listOf(0.25, 0.75))
        checkStatVarValues(statDf, Stats.PROPPCT, listOf(25.0, 75.0))
    }

    @Test
    fun normalizeByNotNumericValues() {
        val dataAfterStat = mapOf(
            Stats.X to listOf("0.0"),
            Stats.COUNT to listOf(1.0),
        )
        val builder = DataFrame.Builder()
        for (key in dataAfterStat.keys) {
            builder.put(key, dataAfterStat.getValue(key))
        }

        val stat = Stats.count2d()
        val statDf = stat.normalize(dataAfterStat = builder.build())

        checkStatVarValues(statDf, Stats.COUNT, listOf(1.0))
        checkStatVarValues(statDf, Stats.SUM, listOf(1.0))
        checkStatVarValues(statDf, Stats.PROP, listOf(1.0))
        checkStatVarValues(statDf, Stats.PROPPCT, listOf(100.0))
    }
}