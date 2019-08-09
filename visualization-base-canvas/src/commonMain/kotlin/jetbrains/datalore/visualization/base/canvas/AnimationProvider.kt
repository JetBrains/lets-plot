package jetbrains.datalore.visualization.base.canvas

interface AnimationProvider {
    fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer

    interface AnimationTimer {
        fun start()
        fun stop()
    }

    interface AnimationEventHandler {
        fun onEvent(millisTime: Long): Boolean
    }
}