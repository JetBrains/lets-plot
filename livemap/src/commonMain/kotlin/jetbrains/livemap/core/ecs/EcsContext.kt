package jetbrains.livemap.core.ecs

import jetbrains.livemap.core.SystemTime
import jetbrains.datalore.visualization.plot.base.event.MouseEventSource
import jetbrains.livemap.core.MetricsService

class EcsContext(val eventSource: MouseEventSource) : EcsClock {
    override val systemTime = SystemTime()
    val metricsService = MetricsService()

    override var updateStartTime: Long = 0
        private set

    var tick: Long = 0

    internal fun startUpdate() {
        tick++
        updateStartTime = systemTime.getTimeMs()
    }
}