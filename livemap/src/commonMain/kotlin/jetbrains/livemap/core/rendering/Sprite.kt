package jetbrains.livemap.core.rendering

import jetbrains.datalore.visualization.base.canvas.Canvas.Snapshot

class Sprite {
    var snapshot: Snapshot? = null
        set(snapshot) {
            field = snapshot
            myStatus = SpriteStatus.READY
        }
    private var myStatus = SpriteStatus.EMPTY

    fun isEmpty() = myStatus == SpriteStatus.EMPTY

    fun isReady() = myStatus == SpriteStatus.READY

    internal enum class SpriteStatus {
        EMPTY,
        READY
    }
}