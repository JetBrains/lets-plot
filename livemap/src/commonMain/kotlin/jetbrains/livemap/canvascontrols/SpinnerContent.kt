/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvascontrols

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.datalore.vis.canvas.*
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.vis.canvas.CanvasControlUtil.setAnimationHandler
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

                    takeSnapshot().onSuccess(canvasControl.context::drawImage)
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
        setFont(
            Font(
            fontSize = FONT_SIZE,
            fontFamily = "Helvetica, Arial, sans-serif")
        )

        val textWidth = measureText(LOADING_TEXT)

        val spinnerWidth = 2 * RADIUS + LINE_WIDTH
        val width = spinnerWidth + SPACE + textWidth
        val dimension = canvasControl.size
        spinnerCenter = DoubleVector((dimension.x - width) / 2 + spinnerWidth / 2, dimension.y / 2.0)

        setFillStyle(BACKGROUND_COLOR)
        fillRect(0.0, 0.0, dimension.x.toDouble(), dimension.y.toDouble())

        setTextBaseline(TextBaseline.MIDDLE)
        setTextAlign(TextAlign.START)
        setFillStyle(FONT_COLOR)
        fillText(LOADING_TEXT, (dimension.x + width) / 2 - textWidth, dimension.y / 2.0)

        restore()
    }

    private fun Context2d.drawSpinner(time: Long) {
        save()

        setFillStyle(BACKGROUND_COLOR)
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

    private fun Context2d.drawSpinnerArc(color: Color, startAngle: Double, arcAngle: Double) {
        setLineWidth(LINE_WIDTH)
        setStrokeStyle(color)
        beginPath()
        arc(spinnerCenter.x, spinnerCenter.y, RADIUS, startAngle, startAngle + arcAngle)
        stroke()
    }

    companion object {
        private val BACKGROUND_COLOR = Color.WHITE
        private val CIRCLE_COLOR = Color(232, 232, 232)
        private val FONT_COLOR = Color(97, 97, 97)
        private val ARC_COLOR = Color(0, 191, 255)
        private const val LINE_WIDTH = 0.9
        private const val RADIUS = 11.5
        private const val BACK_RADIUS = RADIUS + LINE_WIDTH
        private const val ARC_LENGTH = PI / 2
        private const val LOOP_DURATION: Long = 1000
        private const val SPACE = 15.0
        private const val LOADING_TEXT = "Loading..."
        private const val FONT_SIZE = 12.0
    }
}