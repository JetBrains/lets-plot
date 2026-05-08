/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.batik.plot.util

import demoAndTestShared.SimpleTestSpecs.simpleBunch
import demoAndTestShared.SimpleTestSpecs.simplePlot
import demoAndTestShared.SimpleTestSpecs.simplePointLayer
import org.jetbrains.letsPlot.awt.plot.MonolithicAwt
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEvent.Companion.leftButton
import org.jetbrains.letsPlot.commons.event.MouseEvent.Companion.noButton
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventSpec
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventSpec.*
import org.jetbrains.letsPlot.datamodel.svg.util.SvgToString
import kotlin.math.abs
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.StringReader
import javax.swing.JComponent
import javax.swing.JLabel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

typealias AWTMouseEvent = java.awt.event.MouseEvent

class BatikPlotComponentTest {

    @Test
    fun svgColorAlphaIsRendered() {
        val svg = SvgSvgElement(4.0, 4.0).apply {
            children().add(SvgRectElement(0.0, 0.0, 4.0, 4.0).apply {
                fillColor().set(Color.WHITE)
            })
            children().add(SvgRectElement(0.0, 0.0, 4.0, 4.0).apply {
                fillColor().set(Color(255, 0, 0, 128))
            })
        }
        val svgText = SvgToString.render(svg)

        assertTrue(svgText.contains("fill=\"rgb(255,0,0)\""))
        assertTrue(svgText.contains("fill-opacity="))

        val image = renderSvg(svgText)

        val pixel = image.getRGB(2, 2)
        assertEquals(255, pixel shr 16 and 0xff)
        assertClose(127, pixel shr 8 and 0xff)
        assertClose(127, pixel and 0xff)
    }

    @Test
    fun svgEventsTest() {
        val eventsLog = mutableListOf<Pair<SvgEventSpec, MouseEvent>>()
        fun eq(expected: Pair<SvgEventSpec, MouseEvent>, actual: Pair<SvgEventSpec, MouseEvent>) {
            val (expectedSpec, expectedEvent) = expected
            val (actualSpec, actualEvent) = actual

            assertEquals(expectedSpec, actualSpec)
            assertEquals(expectedEvent.x, actualEvent.x)
            assertEquals(expectedEvent.y, actualEvent.y)
            assertEquals(expectedEvent.button, actualEvent.button)
        }

        val svgRoot = SvgSvgElement(400.0, 200.0).apply {
            id().set("root")
            children().add(
                SvgEllipseElement(100.0, 50.0, 50.0, 50.0).apply {
                    id().set("ellipse")
                    addEventHandler<MouseEvent>(MOUSE_PRESSED) { _, e -> eventsLog.add(MOUSE_PRESSED to e) }
                    addEventHandler<MouseEvent>(MOUSE_RELEASED) { _, e -> eventsLog.add(MOUSE_RELEASED to e) }
                    addEventHandler<MouseEvent>(MOUSE_OUT) { _, e -> eventsLog.add(MOUSE_OUT to e) }
                    addEventHandler<MouseEvent>(MOUSE_OVER) { _, e -> eventsLog.add(MOUSE_OVER to e) }
                    addEventHandler<MouseEvent>(MOUSE_MOVE) { _, e -> eventsLog.add(MOUSE_MOVE to e) }
                    addEventHandler(MOUSE_CLICKED) { _: SvgNode, e: MouseEvent ->
                        eventsLog.add(MOUSE_CLICKED to e)
                        children().add(SvgCircleElement(e.x.toDouble(), e.y.toDouble(), 5.0).apply {
                            id().set("circle")
                        })
                    }
                })
        }

        val component = BatikMapperComponent(svgRoot, BATIK_MESSAGE_CALLBACK)

        fun dispatch(
            id: Int,
            x: Int,
            y: Int,
            button: Int = AWTMouseEvent.NOBUTTON
        ): List<Pair<SvgEventSpec, MouseEvent>> {
            eventsLog.clear()
            component.dispatchEvent(AWTMouseEvent(component, id, 1L, 0, x, y, 1, false, button))
            return eventsLog.toList()
        }

        assertNull(SvgUtils.findNodeById(svgRoot, "circle"))

        // Move cursor outside the ellipse
        assertTrue(dispatch(AWTMouseEvent.MOUSE_MOVED, 10, 10).isEmpty())

        // Click outside the ellipse
        assertTrue(dispatch(AWTMouseEvent.MOUSE_CLICKED, 10, 10).isEmpty())

        assertNull(SvgUtils.findNodeById(svgRoot, "circle"))

        // Move cursor inside the ellipse
        dispatch(AWTMouseEvent.MOUSE_MOVED, 100, 50).let {
            eq(MOUSE_OVER to noButton(Vector(100, 50)), it[0])
            eq(MOUSE_MOVE to noButton(Vector(100, 50)), it[1])
        }

        // Click inside the ellipse
        dispatch(AWTMouseEvent.MOUSE_PRESSED, 100, 50, AWTMouseEvent.BUTTON1).let { ellipseEvents ->
            eq(MOUSE_PRESSED to leftButton(Vector(100, 50)), ellipseEvents[0])
        }

        dispatch(AWTMouseEvent.MOUSE_RELEASED, 100, 50, AWTMouseEvent.BUTTON1).let { ellipseEvents ->
            eq(MOUSE_RELEASED to leftButton(Vector(100, 50)), ellipseEvents[0])
        }

        dispatch(AWTMouseEvent.MOUSE_CLICKED, 100, 50, AWTMouseEvent.BUTTON1).let { ellipseEvents ->
            eq(MOUSE_CLICKED to leftButton(Vector(100, 50)), ellipseEvents[0])
        }

        SvgUtils.findNodeById(svgRoot, "circle").let {
            assertTrue(it is SvgCircleElement)
            assertEquals(100.0, it.cx().get())
            assertEquals(50.0, it.cy().get())
        }

        // Move cursor outside the ellipse
        dispatch(AWTMouseEvent.MOUSE_MOVED, 10, 10).let { ellipseEvents ->
            eq(MOUSE_OUT to noButton(Vector(10, 10)), ellipseEvents[0])
        }
    }

    @Test
    internal fun isDisposable_singlePlot() {
        val plotSpec = simplePlot(listOf(simplePointLayer()))
        val component = buildPlotFromRawSpecs(plotSpec)

        assertDisposable(component)
        (component as Disposable).dispose()
    }

    @Test
    internal fun isDisposable_bunch() {
        val geoms = listOf(simplePointLayer(), simplePointLayer())
        val plotSpec = simpleBunch(geoms)
        val component = buildPlotFromRawSpecs(plotSpec)

        assertDisposable(component)
        (component as Disposable).dispose()
    }

    private fun assertDisposable(o: Any) {
        assertTrue(o is Disposable, "Expected Disposable, was ${o::class.simpleName}")
    }

    companion object {
        private val BATIK_MESSAGE_CALLBACK = object : BatikMessageCallback {
            override fun handleMessage(message: String) {
                throw RuntimeException("Unexpected Batik message: $message")
            }

            override fun handleException(e: Exception) {
                throw RuntimeException("Unexpected Batik exception", e)
            }
        }

        private val COMPONENT_FACTORY = { svg: SvgSvgElement ->
            BatikMapperComponent(svg, BATIK_MESSAGE_CALLBACK)
        }

        private val AWT_EDT_EXECUTOR = { runnable: () -> Unit ->
            // Just invoke in the current thread.
            runnable.invoke()
        }

        private fun buildPlotFromRawSpecs(plotSpec: MutableMap<String, Any>): JComponent {
            val component = MonolithicAwt.buildPlotFromRawSpecs(
                plotSpec = plotSpec,
                containerSize = null,
                sizingPolicy = SizingPolicy.keepFigureDefaultSize(),
                svgComponentFactory = COMPONENT_FACTORY,
                executor = AWT_EDT_EXECUTOR
            ) {
                if (it.isNotEmpty()) {
                    throw RuntimeException("Unexpected computation messages: $it")
                }
            }

            if (component is JLabel) {
                throw RuntimeException("Error while building plot: ${component.text}")
            }
            return component
        }

        private fun renderSvg(svg: String): BufferedImage {
            lateinit var image: BufferedImage
            val transcoder = object : ImageTranscoder() {
                override fun createImage(width: Int, height: Int): BufferedImage {
                    return BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
                }

                override fun writeImage(img: BufferedImage, output: TranscoderOutput) {
                    image = img
                }
            }
            transcoder.transcode(TranscoderInput(StringReader(svg)), TranscoderOutput(ByteArrayOutputStream()))
            return image
        }

        private fun assertClose(expected: Int, actual: Int) {
            assertTrue(abs(expected - actual) <= 1, "Expected $actual to be within 1 of $expected")
        }

    }
}
