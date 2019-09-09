package jetbrains.datalore.visualization.plot.base.stat

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.Stat

abstract class BaseStat(private val defaultMappings: Map<Aes<*>, DataFrame.Variable>) : Stat {

    override fun hasDefaultMapping(aes: Aes<*>): Boolean {
        return defaultMappings.containsKey(aes)
    }

    override fun getDefaultMapping(aes: Aes<*>): DataFrame.Variable {
        if (defaultMappings.containsKey(aes)) {
            return defaultMappings[aes]!!
        }
        throw IllegalArgumentException("Stat " + this::class.simpleName + " has no default mapping for aes: " + aes)
    }

    protected fun withEmptyStatValues(): DataFrame {
        val newData = DataFrame.Builder()
        for (aes in Aes.values()) {
            if (hasDefaultMapping(aes)) {
                newData.put(getDefaultMapping(aes), emptyList<Any>())
            }
        }
        return newData.build()
    }
}
