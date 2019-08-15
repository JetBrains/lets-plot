package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator

interface BaseLiveMapFactory {
    fun createGeomTargetLocator(): GeomTargetLocator

    fun createLiveMap(): Async<BaseLiveMap>
}