/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.awt.canvas


import jetbrains.datalore.base.registration.Disposable
import java.awt.EventQueue
import java.awt.event.ActionListener
import javax.swing.Timer

class AwtAnimationTimerPeer(
    val executor: (() -> Unit) -> Unit = { f -> EventQueue.invokeLater { f() } },
    private val updateRate: Int = 60
) : Disposable {
    private val myHandlers = ArrayList<(Long) -> Unit>()

    private var actionListener = ActionListener {
        myHandlers.forEach {
            executor {
                it(System.currentTimeMillis())
            }
        }
    }

    private val myTimer: Timer = Timer(1000 / updateRate, actionListener)

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