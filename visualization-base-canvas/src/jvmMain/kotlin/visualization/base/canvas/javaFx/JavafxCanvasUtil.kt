package jetbrains.datalore.visualization.base.canvas.javaFx

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.SimpleAsync
import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.function.Predicate
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

internal object JavafxCanvasUtil {
    private val EVENT_SPEC_MAP = mapOf(
            MouseEventSpec.MOUSE_ENTERED to eventOptions(JavafxEventPeer.JavafxEventSpec.MOUSE_ENTERED),
            MouseEventSpec.MOUSE_LEFT to eventOptions(JavafxEventPeer.JavafxEventSpec.MOUSE_EXITED),
            MouseEventSpec.MOUSE_MOVED to eventOptions(JavafxEventPeer.JavafxEventSpec.MOUSE_MOVED),
            MouseEventSpec.MOUSE_DRAGGED to eventOptions(JavafxEventPeer.JavafxEventSpec.MOUSE_DRAGGED),
            MouseEventSpec.MOUSE_CLICKED to eventOptions(JavafxEventPeer.JavafxEventSpec.MOUSE_CLICKED) { e -> e.clickCount % 2 == 1 },
            MouseEventSpec.MOUSE_DOUBLE_CLICKED to eventOptions(JavafxEventPeer.JavafxEventSpec.MOUSE_CLICKED) { e -> e.clickCount % 2 == 0 },
            MouseEventSpec.MOUSE_PRESSED to eventOptions(JavafxEventPeer.JavafxEventSpec.MOUSE_PRESSED),
            MouseEventSpec.MOUSE_RELEASED to eventOptions(JavafxEventPeer.JavafxEventSpec.MOUSE_RELEASED))

    private val MOUSE_BUTTON_MAP = mapOf(
            MouseButton.NONE to Button.NONE,
            MouseButton.PRIMARY to Button.LEFT,
            MouseButton.MIDDLE to Button.MIDDLE,
            MouseButton.SECONDARY to Button.RIGHT
    )

    //works only in javafx thread
    fun takeSnapshotImage(canvas: Node): WritableImage {
        val params = SnapshotParameters()
        params.fill = Color.TRANSPARENT
        return canvas.snapshot(params,
                null)
    }

    fun asyncTakeSnapshotImage(canvas: Canvas): Async<WritableImage> {
        val async = SimpleAsync<WritableImage>()

        runInJavafxThread(Runnable {
            val params = SnapshotParameters()
            params.fill = Color.TRANSPARENT
            canvas.snapshot(
                    { param ->
                        async.success(param.image)
                        null
                    },
                    params, null
            )
        })

        return async
    }

    private fun runInJavafxThread(runnable: Runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run()
        } else {
            Platform.runLater(runnable)
        }
    }

    fun addMouseEventHandler(
            eventPeer: JavafxEventPeer,
            eventSpec: MouseEventSpec,
            eventHandler: EventHandler<jetbrains.datalore.base.event.MouseEvent>
    ): Registration {
        val eventOptions = EVENT_SPEC_MAP[eventSpec]
        return eventPeer.addEventHandler(eventOptions!!.spec,
                object : EventHandler<MouseEvent> {
                    override fun onEvent(event: MouseEvent) {
                        if (eventOptions.predicate(event)) {
                            eventHandler.onEvent(createMouseEvent(event))
                        }
                    }
                })
    }

    fun imagePngBase64ToImage(dataUrl: String): Image {
        val mediaType = "data:image/png;base64,"
        val imageString = dataUrl.replace(mediaType, "")

        val bytes = imageString.toByteArray(StandardCharsets.UTF_8)
        val byteArrayInputStream = ByteArrayInputStream(bytes)

        try {
            Base64.getDecoder().wrap(byteArrayInputStream).use { wrap -> return Image(wrap) }
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    private fun createMouseEvent(e: MouseEvent): jetbrains.datalore.base.event.MouseEvent {
        return jetbrains.datalore.base.event.MouseEvent(
                round(e.x),
                round(e.y),
                MOUSE_BUTTON_MAP[e.button],
                createKeyModifiers(e)
        )
    }

    private fun createKeyModifiers(e: MouseEvent): KeyModifiers {
        return KeyModifiers(e.isControlDown, e.isAltDown, e.isShiftDown, e.isMetaDown)
    }

    private fun round(v: Double): Int {
        return kotlin.math.round(v).toInt()
    }

    private fun eventOptions(
            eventSpec: JavafxEventPeer.JavafxEventSpec,
            eventPredicate: Predicate<MouseEvent>): JavafxEventOptions {
        return JavafxEventOptions(eventSpec, eventPredicate)
    }

    private fun eventOptions(
            eventSpec: JavafxEventPeer.JavafxEventSpec): JavafxEventOptions {
        return JavafxEventOptions(eventSpec) { true }
    }

    private class JavafxEventOptions
    internal constructor(
            internal val spec: JavafxEventPeer.JavafxEventSpec,
            internal val predicate: Predicate<MouseEvent>)
}
