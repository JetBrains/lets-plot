package jetbrains.datalore.vis.canvas

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.Vector

interface Canvas {
    val context2d: Context2d

    val size: Vector

    fun takeSnapshot(): Async<Snapshot>

    interface Snapshot
}
