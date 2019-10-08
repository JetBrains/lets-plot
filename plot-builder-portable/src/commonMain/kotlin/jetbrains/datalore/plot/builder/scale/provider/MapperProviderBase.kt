package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.plot.builder.scale.MapperProviderAdapter

abstract class MapperProviderBase<T>(protected val naValue: T) : MapperProviderAdapter<T>()
