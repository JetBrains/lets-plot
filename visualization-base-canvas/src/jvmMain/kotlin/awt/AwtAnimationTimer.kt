package jetbrains.datalore.visualization.base.canvas.awt

import jetbrains.datalore.base.edt.AwtEventDispatchThread
import jetbrains.datalore.base.function.Runnable
import jetbrains.datalore.visualization.base.canvas.CanvasControl.AnimationTimer
import java.util.*

internal abstract class AwtAnimationTimer : AnimationTimer {
    private val myTimer = Timer()

    internal abstract fun handle(millisTime: Long)

    override fun start() {
        myTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                AwtEventDispatchThread.INSTANCE.schedule(object : Runnable {
                    override fun run() {
                        handle(AwtEventDispatchThread.INSTANCE.currentTimeMillis)
                    }
                })
            }
        }, 0, 10)
    }

    override fun stop() {
        myTimer.cancel()
    }
}
