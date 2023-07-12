/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.canvas

import org.jetbrains.letsPlot.core.canvas.AnimationProvider.AnimationTimer
import kotlinx.browser.window

internal abstract class DomAnimationTimer : AnimationTimer {
    private var myHandle: Int? = null
    private var myIsStarted: Boolean = false

    init {
        myIsStarted = false
    }

    internal abstract fun handle(millisTime: Long)

    override fun start() {
        if (myIsStarted) {
            return
        }

        myIsStarted = true
        requestNextFrame()
    }

    override fun stop() {
        if (!myIsStarted) {
            return
        }

        myIsStarted = false
        window.cancelAnimationFrame(myHandle!!)
    }

    private fun execute(millisTime: Double) {
        if (!myIsStarted) {
            return
        }

        handle(millisTime.toLong())

        requestNextFrame()
    }

    private fun requestNextFrame() {
        myHandle = window.requestAnimationFrame { this.execute(it) }
    }
}
