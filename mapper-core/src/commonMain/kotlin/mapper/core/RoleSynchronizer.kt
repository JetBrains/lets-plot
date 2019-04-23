package jetbrains.datalore.mapper.core

/**
 * Synchronizer which creates child mappers for objects contained in a role.
 *
 *
 * Role might be:
 * - a property
 * - a collections
 *
 * @param <SourceT> - context object's type
 * @param <TargetT> - type of objects which are contained in the role
</TargetT></SourceT> */
interface RoleSynchronizer<SourceT, TargetT> : Synchronizer {
    val mappers: List<Mapper<out SourceT, out TargetT>>

    fun addMapperFactory(factory: MapperFactory<SourceT, TargetT>)
    fun addErrorMapperFactory(factory: MapperFactory<SourceT, TargetT>)
    fun addMapperProcessor(processor: MapperProcessor<SourceT, TargetT>)
}
