/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.ecs

import org.jetbrains.letsPlot.commons.SystemTime
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.livemap.core.MetricsService

open class EcsContext(
    val eventSource: MouseEventSource
) : EcsClock {
    override val systemTime = SystemTime()
    override var frameStartTimeMs: Long = 0
    override val frameDurationMs: Long get() = systemTime.getTimeMs() - frameStartTimeMs

    val metricsService = MetricsService(systemTime)
    var tick: Long = 0

    internal fun startFrame() {
        tick++
        frameStartTimeMs = systemTime.getTimeMs()
    }
}