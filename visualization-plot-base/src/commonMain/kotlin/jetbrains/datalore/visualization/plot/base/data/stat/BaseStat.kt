package jetbrains.datalore.visualization.plot.base.data.stat

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.data.Stat

abstract class BaseStat protected constructor(private val myDefaultMappings: Map<Aes<*>, DataFrame.Variable>) : Stat {

    override fun hasDefaultMapping(aes: Aes<*>): Boolean {
        return myDefaultMappings.containsKey(aes)
    }

    override fun getDefaultMapping(aes: Aes<*>): DataFrame.Variable {
        if (myDefaultMappings.containsKey(aes)) {
            return myDefaultMappings[aes]!!
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
