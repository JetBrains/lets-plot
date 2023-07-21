/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.multitasking

import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.SimpleEventSource
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.livemap.core.SystemTime
import kotlin.math.max

class DebugMicroTask<ItemT>(
    private val mySystemTime: SystemTime,
    private val myMicroTask: MicroTask<ItemT>
) : MicroTask<ItemT> {

    private val finishEventSource = SimpleEventSource<Unit?>()

    var processTime: Long = 0
        private set
    var maxResumeTime: Long = 0
        private set


    override fun resume() {
        val start = mySystemTime.getTimeMs()
        myMicroTask.resume()
        val resumeTime = mySystemTime.getTimeMs() - start

        processTime += resumeTime
        maxResumeTime = max(resumeTime, maxResumeTime)

        if (!myMicroTask.alive()) {
            finishEventSource.fire(null)
        }
    }

    fun addFinishHandler(handler: () -> Unit): Registration {
        return finishEventSource.addHandler(object : EventHandler<Unit?> {
            override fun onEvent(event: Unit?) {
                handler()
            }
        })
    }

    override fun alive(): Boolean = myMicroTask.alive()

    override fun getResult(): ItemT = myMicroTask.getResult()
}