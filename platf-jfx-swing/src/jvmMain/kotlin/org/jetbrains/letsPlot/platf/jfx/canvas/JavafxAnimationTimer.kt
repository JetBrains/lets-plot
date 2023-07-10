/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.jfx.canvas

import jetbrains.datalore.vis.canvas.AnimationProvider

internal abstract class JavafxAnimationTimer : AnimationProvider.AnimationTimer {
    private val myAnimationTimer: javafx.animation.AnimationTimer

    init {
        myAnimationTimer = object : javafx.animation.AnimationTimer() {

            override fun handle(nanoTime: Long) {
                this@JavafxAnimationTimer.handle((nanoTime / 1.0e6).toLong())
            }
        }
    }

    internal abstract fun handle(millisTime: Long)

    override fun start() {
        myAnimationTimer.start()
    }

    override fun stop() {
        myAnimationTimer.stop()
    }
}