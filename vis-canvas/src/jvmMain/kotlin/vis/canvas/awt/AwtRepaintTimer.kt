/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.awt


import java.awt.event.ActionListener
import javax.swing.Timer
import kotlin.collections.ArrayList

class AwtRepaintTimer(private val repaint: () -> Unit) {
    private val myHandlers = ArrayList<(Long) -> Unit>()

    private val actionListener = ActionListener {
        myHandlers.forEach {
            it(System.currentTimeMillis())
        }
        repaint()
    }

    private val myTimer: Timer = Timer(1000 / 60, actionListener)

    fun addHandler(handler: (Long) -> Unit) {
        synchronized(myHandlers) {
            myHandlers.add(handler)

            if (!myTimer.isRunning) {
                myTimer.start()
            }
        }

    }

    fun removeHandler(handler: (Long) -> Unit) {
        synchronized(myHandlers) {
            myHandlers.remove(handler)

            if (myHandlers.isEmpty() && myTimer.isRunning) {
                myTimer.stop()
            }
        }
    }
}