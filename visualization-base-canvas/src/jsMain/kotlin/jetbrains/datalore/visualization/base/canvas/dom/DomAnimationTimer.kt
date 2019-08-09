package jetbrains.datalore.visualization.base.canvas.dom

import jetbrains.datalore.base.js.dom.DomElement
import jetbrains.datalore.base.js.dom.DomWindow
import jetbrains.datalore.visualization.base.canvas.AnimationProvider.AnimationTimer

internal abstract class DomAnimationTimer(private val myElement: DomElement) : AnimationTimer {
    private var myHandle: Int? = null
    private var myIsStarted: Boolean = false

    init {
        myIsStarted = false
    }

    internal abstract fun handle(millisTime: Long)

    override fun start() {
        if (myIsStarted) {
            return
        }

        myIsStarted = true
        requestNextFrame()
    }

    override fun stop() {
        if (!myIsStarted) {
            return
        }

        myIsStarted = false
        DomWindow.getWindow().cancelAnimationFrame(myHandle!!)
    }

    fun execute(millisTime: Double) {
        if (!myIsStarted) {
            return
        }

        handle(millisTime.toLong())

        requestNextFrame()
    }

    private fun requestNextFrame() {
        myHandle = DomWindow.getWindow().requestAnimationFrame { this.execute(it) }
    }
}
