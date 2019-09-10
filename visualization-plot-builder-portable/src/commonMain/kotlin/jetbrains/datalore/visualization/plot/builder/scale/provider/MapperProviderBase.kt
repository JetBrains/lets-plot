package jetbrains.datalore.visualization.plot.builder.scale.provider

import jetbrains.datalore.visualization.plot.builder.scale.MapperProviderAdapter

abstract class MapperProviderBase<T>(protected val naValue: T) : MapperProviderAdapter<T>()
