package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.coroutines.*
import org.jetbrains.letsPlot.core.canvas.AnimationProvider
import org.jetbrains.letsPlot.core.canvas.AnimationProvider.AnimationEventHandler
import org.jetbrains.letsPlot.core.canvas.AnimationProvider.AnimationTimer
import kotlin.time.TimeSource

class NativeAnimationProvider(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : AnimationProvider {

    override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
        return CoroutineAnimationTimer(eventHandler, scope)
    }

    private class CoroutineAnimationTimer(
        private val handler: AnimationEventHandler,
        private val scope: CoroutineScope
    ) : AnimationTimer {

        private var job: Job? = null
        private val timeSource = TimeSource.Monotonic
        private val startTime = timeSource.markNow()

        override fun start() {
            if (job?.isActive == true) return

            job = scope.launch {
                while (isActive) {
                    val nowMillis = startTime.elapsedNow().inWholeMilliseconds

                    val continueAnimation = try {
                        handler.onEvent(nowMillis)
                    } catch (e: Exception) {
                        println("Error in animation loop: ${e.message}")
                        false
                    }

                    //if (!continueAnimation) {
                    //    println("AnimationTimer canceled - ${timeSource.markNow()}")
                    //    this.cancel()
                    //    break
                    //}

                    delay(16)
                }
            }
        }

        override fun stop() {
            job?.cancel()
            job = null
        }
    }
}
