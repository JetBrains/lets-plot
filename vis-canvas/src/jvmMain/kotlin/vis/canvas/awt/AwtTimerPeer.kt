/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.awt


import jetbrains.datalore.base.registration.Disposable
import java.awt.EventQueue
import java.awt.event.ActionListener
import javax.swing.Timer

class AwtTimerPeer(
    val executor: (() -> Unit) -> Unit = { f -> EventQueue.invokeLater { f() } },
) : Disposable {
    private val myHandlers = ArrayList<(Long) -> Unit>()

    private var actionListener = ActionListener {
        myHandlers.forEach {
            executor {
                it(System.currentTimeMillis())
            }
        }
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

    override fun dispose() {
        myTimer.stop()
        myTimer.removeActionListener(actionListener)
        actionListener = ActionListener {}
        myHandlers.clear()
    }
}