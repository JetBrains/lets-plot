package jetbrains.livemap.core.animation

interface Animation {
    val isFinished: Boolean
    val duration: Double
    var time: Double
    fun animate()

    enum class Direction {
        FORWARD,
        BACK
    }

    enum class Loop {
        DISABLED,
        SWITCH_DIRECTION,
        KEEP_DIRECTION
    }
}
