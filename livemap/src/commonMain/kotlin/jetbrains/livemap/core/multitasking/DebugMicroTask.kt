package jetbrains.livemap.core.multitasking

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.SimpleEventSource
import jetbrains.datalore.base.registration.Registration
import jetbrains.livemap.core.SystemTime
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