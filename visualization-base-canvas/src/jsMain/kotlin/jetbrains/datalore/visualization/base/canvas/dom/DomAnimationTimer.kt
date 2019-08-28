package jetbrains.datalore.visualization.base.canvas.dom

import jetbrains.datalore.visualization.base.canvas.AnimationProvider.AnimationTimer
import org.w3c.dom.Element
import kotlin.browser.window

internal abstract class DomAnimationTimer(private val myElement: Element) : AnimationTimer {
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
        window.cancelAnimationFrame(myHandle!!)
    }

    fun execute(millisTime: Double) {
        if (!myIsStarted) {
            return
        }

        handle(millisTime.toLong())

        requestNextFrame()
    }

    private fun requestNextFrame() {
        myHandle = window.requestAnimationFrame { this.execute(it) }
    }
}
