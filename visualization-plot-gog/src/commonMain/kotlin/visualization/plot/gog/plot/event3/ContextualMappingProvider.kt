package jetbrains.datalore.visualization.plot.gog.plot.event3

import jetbrains.datalore.visualization.plot.gog.core.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.gog.core.event3.ContextualMapping

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
