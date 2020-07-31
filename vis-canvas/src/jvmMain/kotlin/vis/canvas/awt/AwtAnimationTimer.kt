/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.awt

import jetbrains.datalore.vis.canvas.AnimationProvider
import java.util.*

internal abstract class AwtAnimationTimer : AnimationProvider.AnimationTimer {
    private val myTimer: Timer = Timer()
    private val myTimerTask = object : TimerTask() {
        override fun run() {
            this@AwtAnimationTimer.handle(System.currentTimeMillis())
        }
    }

    internal abstract fun handle(millisTime: Long)

    override fun start() {
        myTimer.scheduleAtFixedRate(
            myTimerTask, 0, 1000 / 30
        )
    }

    override fun stop() {
        myTimer.cancel()
    }
}