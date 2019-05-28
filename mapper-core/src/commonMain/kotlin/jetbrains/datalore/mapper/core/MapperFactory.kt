package jetbrains.datalore.mapper.core

interface MapperFactory<SourceT, TargetT> {
    fun createMapper(source: SourceT): Mapper<out SourceT, out TargetT>
}