package jetbrains.datalore.visualization.plot.gog.plot.scale.provider

import jetbrains.datalore.visualization.plot.gog.plot.scale.MapperProviderAdapter

internal abstract class MapperProviderBase<T>(protected val naValue: T) : MapperProviderAdapter<T>() {
}
