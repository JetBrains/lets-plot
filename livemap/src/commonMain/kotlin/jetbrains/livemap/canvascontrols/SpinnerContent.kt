package jetbrains.livemap.canvascontrols

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.base.canvas.CanvasControlUtil.setAnimationHandler
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.datalore.visualization.base.canvas.SingleCanvasControl
import kotlin.math.PI

internal class SpinnerContent : CanvasContent {

    private lateinit var registration: Registration
    private lateinit var canvasControl: SingleCanvasControl
    private lateinit var spinnerCenter: DoubleVector

    override fun show(parentControl: CanvasControl) {
        canvasControl = SingleCanvasControl(parentControl)

        with(canvasControl.createCanvas()) {
            context2d.drawStaticElements()

            registration = setAnimationHandler(
                parentControl,
                AnimationEventHandler.toHandler { millisTime: Long ->
                    context2d.drawSpinner(millisTime)

                    takeSnapshot()
                        .onSuccess { canvasControl.context.drawImage(it, 0.0, 0.0) }
                    true
                }
            )
        }
    }

    override fun hide() {
        canvasControl.dispose()
        registration.dispose()
    }

    private fun Context2d.drawStaticElements() {
        save()

        setFont("400 " + FONT_SIZE + "px Helvetica, Arial, sans-serif")
        val textWidth = measureText(LOADING_TEXT)

        val spinnerWidth = 2 * RADIUS + LINE_WIDTH
        val width = spinnerWidth + SPACE + textWidth
        val dimension = canvasControl.size
        spinnerCenter = DoubleVector((dimension.x - width) / 2 + spinnerWidth / 2, dimension.y / 2.0)

        setFillColor(BACKGROUND_COLOR)
        fillRect(0.0, 0.0, dimension.x.toDouble(), dimension.y.toDouble())

        setTextBaseline(Context2d.TextBaseline.MIDDLE)
        setTextAlign(Context2d.TextAlign.LEFT)
        setFillColor(FONT_COLOR)
        fillText(LOADING_TEXT, (dimension.x + width) / 2 - textWidth, dimension.y / 2.0)

        restore()
    }

    private fun Context2d.drawSpinner(time: Long) {
        save()

        setFillColor(BACKGROUND_COLOR)
        fillRect(
            spinnerCenter.x - BACK_RADIUS,
            spinnerCenter.y - BACK_RADIUS,
            2 * BACK_RADIUS,
            2 * BACK_RADIUS
        )

        drawSpinnerArc(CIRCLE_COLOR, 0.0, 2 * PI)

        val angle = 2.0 * PI * (time % LOOP_DURATION).toDouble() / LOOP_DURATION
        drawSpinnerArc(ARC_COLOR, angle, ARC_LENGTH)

        restore()
    }

    private fun Context2d.drawSpinnerArc(color: String, startAngle: Double, arcAngle: Double) {
        setLineWidth(LINE_WIDTH)
        setStrokeColor(color)
        beginPath()
        arc(spinnerCenter.x, spinnerCenter.y, RADIUS, startAngle, startAngle + arcAngle)
        stroke()
    }

    companion object {
        private const val BACKGROUND_COLOR = "#FFFFFF"
        private const val LINE_WIDTH = 0.9
        private const val RADIUS = 11.5
        private const val BACK_RADIUS = RADIUS + LINE_WIDTH
        private const val CIRCLE_COLOR = "#E8E8E8"
        private const val ARC_COLOR = "#00BFFF"
        private const val ARC_LENGTH = PI / 2
        private const val LOOP_DURATION: Long = 1000
        private const val SPACE = 15.0
        private const val LOADING_TEXT = "Loading..."
        private const val FONT_SIZE = 12.0
        private const val FONT_COLOR = "#616161"
    }
}