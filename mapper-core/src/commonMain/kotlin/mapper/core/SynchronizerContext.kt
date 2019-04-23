package jetbrains.datalore.mapper.core

/**
 * Context passed to the registerSynchronizers method.
 */
interface SynchronizerContext {
    val mappingContext: MappingContext
    val mapper: Mapper<*, *>
}