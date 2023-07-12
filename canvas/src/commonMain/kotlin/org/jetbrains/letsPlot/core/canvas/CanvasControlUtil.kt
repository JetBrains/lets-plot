/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.AnimationProvider.AnimationEventHandler

object CanvasControlUtil {
    fun drawLater(canvasControl: CanvasControl, renderer: () -> Unit) {
        val reg = arrayOf<Registration?>(null)
        reg[0] = setAnimationHandler(
            canvasControl,
            object : AnimationEventHandler {
                override fun onEvent(millisTime: Long): Boolean {
                    renderer()
                    reg[0]!!.dispose()
                    return true
                }

            })
    }

    fun setAnimationHandler(canvasControl: CanvasControl, eventHandler: AnimationEventHandler): Registration {
        val animationTimer = canvasControl.createAnimationTimer(eventHandler)
        animationTimer.start()
        return Registration.from(object : Disposable {
            override fun dispose() {
                animationTimer.stop()
            }
        })
    }
}
