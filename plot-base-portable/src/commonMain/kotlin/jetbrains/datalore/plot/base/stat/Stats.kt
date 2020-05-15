/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable.Source.STAT
import jetbrains.datalore.plot.base.Stat
import jetbrains.datalore.plot.base.StatContext

object Stats {
    // stat variables can be referenced by name ..name.. (p 54)
    val X = DataFrame.Variable("..x..", STAT, "x")
    val Y = DataFrame.Variable("..y..", STAT, "y")
    val COUNT = DataFrame.Variable("..count..", STAT, "count")
    val DENSITY = DataFrame.Variable("..density..", STAT, "density")
    val Y_MIN = DataFrame.Variable("..ymin..", STAT, "y min")
    val Y_MAX = DataFrame.Variable("..ymax..", STAT, "y max")
    val SE = DataFrame.Variable("..se..", STAT, "standard error")
    val LEVEL = DataFrame.Variable("..level..", STAT, "level")

    val LOWER = DataFrame.Variable("..lower..", STAT, "lower")
    val MIDDLE = DataFrame.Variable("..middle..", STAT, "middle")
    val UPPER = DataFrame.Variable("..upper..", STAT, "upper")
    val WIDTH = DataFrame.Variable("..width..", STAT, "width")

    val SCALED = DataFrame.Variable("..scaled..", STAT, "scaled")

    val GROUP = DataFrame.Variable("..group..", STAT, "group")

    val IDENTITY: Stat = IdentityStat()

    private val VARS: Map<String, DataFrame.Variable> = run {
        val variableList = listOf(
            X,
            Y,
            COUNT,
            DENSITY,
            Y_MIN,
            Y_MAX,
            SE,
            LEVEL,
            LOWER,
            MIDDLE,
            UPPER,
            WIDTH,
            SCALED,
            GROUP
        )

        val result = HashMap<String, DataFrame.Variable>()
        for (variable in variableList) {
            result[variable.name] = variable
        }
        result
    }

    fun isStatVar(varName: String): Boolean {
        return VARS.containsKey(varName)
    }

    fun statVar(varName: String): DataFrame.Variable {
        checkArgument(VARS.containsKey(varName), "Unknown stat variable $varName")
        return VARS[varName]!!
    }

    fun defaultMapping(stat: Stat): Map<Aes<*>, DataFrame.Variable> {
        val map = HashMap<Aes<*>, DataFrame.Variable>()
        for (aes in Aes.values()) {
            if (stat.hasDefaultMapping(aes)) {
                val `var` = stat.getDefaultMapping(aes)
                map[aes] = `var`
            }
        }
        return map
    }

    fun count(): Stat {
        return CountStat()
    }

    fun bin(): BinStatBuilder {
        return BinStatBuilder()
    }

    fun smooth(): SmoothStat {
        return SmoothStat()
    }

    fun contour(): ContourStatBuilder {
        return ContourStatBuilder()
    }

    fun contourf(): ContourfStatBuilder {
        return ContourfStatBuilder()
    }

    fun boxplot(): BoxplotStat {
        return BoxplotStat()
    }

    fun density(): DensityStat {
        return DensityStat()
    }

    fun density2d(): AbstractDensity2dStat {
        return Density2dStat()
    }

    fun density2df(): AbstractDensity2dStat {
        return Density2dfStat()
    }

    private class IdentityStat internal constructor() : BaseStat(emptyMap()) {

        override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
            return DataFrame.Builder.emptyFrame()
        }

        override fun consumes(): List<Aes<*>> {
            return emptyList()
        }
    }
}
