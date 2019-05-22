package jetbrains.datalore.visualization.plot.builder.event3

import jetbrains.datalore.visualization.plot.base.event3.ContextualMapping
import jetbrains.datalore.visualization.plot.base.event3.MappedDataAccess

interface ContextualMappingProvider {
    fun createContextualMapping(dataAccess: MappedDataAccess): ContextualMapping

    companion object {
        val NONE = object : ContextualMappingProvider {
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
