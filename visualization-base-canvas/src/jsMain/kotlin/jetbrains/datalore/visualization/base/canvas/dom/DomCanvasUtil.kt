package jetbrains.datalore.visualization.base.canvas.dom

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.SimpleAsync
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.dom.DomEventUtil.translateInTargetCoord
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import org.w3c.dom.Image


internal object DomCanvasUtil {
    fun addMouseEventHandler(eventPeer: DomEventPeer, eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return eventPeer.addEventHandler(
            eventSpec,
            object : EventHandler<W3cMouseEvent> {
                override fun onEvent(event: W3cMouseEvent) {
                    eventHandler.onEvent(translateInTargetCoord(event))
                }
            }
        )
    }

    fun imagePngBase64ToImage(dataUrl: String): Async<Image> {
        val async = SimpleAsync<Image>()

        val image = Image()

        image.onload = {
            async.success(image)
        }

        image.src = dataUrl

        return async
    }
}
