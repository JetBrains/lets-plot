package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.visualization.plot.base.interact.ContextualMapping
import jetbrains.datalore.visualization.plot.base.interact.MappedDataAccess

interface ContextualMappingProvider {
    fun createContextualMapping(dataAccess: MappedDataAccess): ContextualMapping

    companion object {
        val NONE = object : jetbrains.datalore.plot.builder.interact.ContextualMappingProvider {
            override fun createContextualMapping(dataAccess: MappedDataAccess): ContextualMapping {
                return ContextualMapping(
                        emptyList(),
                        emptyList(),
                        dataAccess
                )
            }
        }
    }
}
