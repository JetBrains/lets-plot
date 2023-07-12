/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.Stat
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.stat.Stats.EMPTY_STATS_DATAFRAME

abstract class BaseStat(private val defaultMappings: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, DataFrame.Variable>) : Stat {
    override fun normalize(dataAfterStat: DataFrame): DataFrame {
        return dataAfterStat
    }

    override fun hasDefaultMapping(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
        return defaultMappings.containsKey(aes)
    }

    override fun getDefaultMapping(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): DataFrame.Variable {
        if (defaultMappings.containsKey(aes)) {
            return defaultMappings[aes]!!
        }
        throw IllegalArgumentException("Stat " + this::class.simpleName + " has no default mapping for aes: " + aes)
    }

    protected fun hasRequiredValues(data: DataFrame, vararg aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
        for (requiredAes in aes) {
            val variable = TransformVar.forAes(requiredAes)
            if (data.hasNoOrEmpty(variable)) {
                return false
            }
        }
        return true
    }

    protected fun withEmptyStatValues(): DataFrame {
        return EMPTY_STATS_DATAFRAME
    }
}
