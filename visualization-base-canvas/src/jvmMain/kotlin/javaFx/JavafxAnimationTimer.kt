package jetbrains.datalore.visualization.base.canvas.javaFx

import jetbrains.datalore.visualization.base.canvas.CanvasControl.AnimationTimer

internal abstract class JavafxAnimationTimer : AnimationTimer {
    private val myAnimationTimer: javafx.animation.AnimationTimer

    init {
        myAnimationTimer = object : javafx.animation.AnimationTimer() {

            override fun handle(nanoTime: Long) {
                this@JavafxAnimationTimer.handle((nanoTime / 1.0e6).toLong())
            }
        }
    }

    internal abstract fun handle(millisTime: Long)

    override fun start() {
        myAnimationTimer.start()
    }

    override fun stop() {
        myAnimationTimer.stop()
    }
}
