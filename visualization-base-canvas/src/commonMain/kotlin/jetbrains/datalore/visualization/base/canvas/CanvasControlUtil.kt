package jetbrains.datalore.visualization.base.canvas

import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.CanvasControl.AnimationEventHandler

object CanvasControlUtil {
    fun drawLater(canvasControl: CanvasControl, renderer: () -> Unit) {
        val reg = arrayOf<Registration?>(null)
        reg[0] = setAnimationHandler(canvasControl, object : AnimationEventHandler {
            override fun onEvent(millisTime: Long): Boolean {
                renderer()
                reg[0]!!.dispose()
                return true
            }

        })
    }

    fun setAnimationHandler(canvasControl: CanvasControl, eventHandler: AnimationEventHandler): Registration {
        val animationTimer = canvasControl.createAnimationTimer(eventHandler)
        animationTimer.start()
        return Registration.from(object : Disposable {
            override fun dispose() {
                animationTimer.stop()
            }
        })
    }
}
