/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering

import jetbrains.datalore.vis.canvas.Canvas.Snapshot

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