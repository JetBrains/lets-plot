package jetbrains.datalore.mapper.core

interface MappingContextListener {
    fun onMapperRegistered(mapper: Mapper<*, *>)
    fun onMapperUnregistered(mapper: Mapper<*, *>)
}