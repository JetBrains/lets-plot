package jetbrains.datalore.mapper.core

/**
 * An entity which gets called when new mapper is attached to a MappingContext.
 * Use it to modifying every mapper view in some way:
 * - install some handlers on a view
 * - pass a configuration into a Mapper
 */
interface MapperProcessor<SourceT, TargetT> {
    fun process(mapper: Mapper<out SourceT, out TargetT>)
}