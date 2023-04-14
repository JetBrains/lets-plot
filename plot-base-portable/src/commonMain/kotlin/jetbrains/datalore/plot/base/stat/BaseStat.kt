/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Stat
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.stat.Stats.EMPTY_STATS_DATAFRAME

abstract class BaseStat(private val defaultMappings: Map<Aes<*>, DataFrame.Variable>) : Stat {
    override fun normalize(dataAfterStat: DataFrame): DataFrame {
        return dataAfterStat
    }

    override fun hasDefaultMapping(aes: Aes<*>): Boolean {
        return defaultMappings.containsKey(aes)
    }

    override fun getDefaultMapping(aes: Aes<*>): DataFrame.Variable {
        if (defaultMappings.containsKey(aes)) {
            return defaultMappings[aes]!!
        }
        throw IllegalArgumentException("Stat " + this::class.simpleName + " has no default mapping for aes: " + aes)
    }

    protected fun hasRequiredValues(data: DataFrame, vararg aes: Aes<*>): Boolean {
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
