package jetbrains.livemap

import jetbrains.datalore.base.async.Async

interface BaseLiveMapFactory {
    fun createLiveMap(): Async<BaseLiveMap>
}