package jetbrains.datalore.base.edt

import jetbrains.datalore.base.function.Runnable
import jetbrains.datalore.base.registration.Registration
import javax.swing.SwingUtilities
import javax.swing.Timer


class AwtEventDispatchThread private constructor() : DefaultAsyncEdt() {
    companion object {
        val INSTANCE = AwtEventDispatchThread()
    }

    override val currentTimeMillis: Long
        get() = System.currentTimeMillis()

    override fun schedule(r: Runnable) {
        SwingUtilities.invokeLater { r.run() }
    }

    override fun schedule(delay: Int, r: Runnable): Registration {
        val timer = Timer(delay, null)
        timer.isRepeats = false
        timer.addActionListener { r.run() }
        timer.start()
        return timerReg(timer)
    }

    override fun scheduleRepeating(period: Int, r: Runnable): Registration {
        val timer = Timer(period, null)
        timer.addActionListener { r.run() }
        timer.start()
        return timerReg(timer)
    }

    private fun timerReg(timer: Timer): Registration {
        return object : Registration() {
            override fun doRemove() {
                if (timer.isRunning) {
                    timer.stop()
                }
            }
        }
    }
}
