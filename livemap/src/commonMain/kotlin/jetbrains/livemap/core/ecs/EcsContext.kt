package jetbrains.livemap.core.ecs

import jetbrains.datalore.base.event.MouseEventSource
import jetbrains.livemap.core.MetricsService
import jetbrains.livemap.core.SystemTime

open class EcsContext(val eventSource: MouseEventSource) : EcsClock {
    override val systemTime
        get() = mySystemTime

    private val mySystemTime = SystemTime()

    val metricsService = MetricsService(mySystemTime)

    override var updateStartTime: Long = 0

    var tick: Long = 0

    internal fun startUpdate() {
        tick++
        updateStartTime = systemTime.getTimeMs()
    }
}