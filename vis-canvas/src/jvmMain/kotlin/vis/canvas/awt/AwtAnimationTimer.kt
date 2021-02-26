/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.awt

import jetbrains.datalore.vis.canvas.AnimationProvider

internal abstract class AwtAnimationTimer(private val myTimer: AwtRepaintTimer) : AnimationProvider.AnimationTimer {
    internal abstract fun handle(millisTime: Long)

    override fun start() {
        myTimer.addHandler(::handle)
    }

    override fun stop() {
        myTimer.removeHandler(::handle)
    }
}