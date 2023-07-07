/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.plot.SimpleTestSpecs.simpleBunch
import jetbrains.datalore.plot.SimpleTestSpecs.simplePlot
import jetbrains.datalore.plot.SimpleTestSpecs.simplePointLayer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import jetbrains.datalore.vis.swing.BatikMapperComponent
import jetbrains.datalore.vis.swing.BatikMessageCallback
import org.jetbrains.letsPlot.platf.awt.plot.MonolithicAwt
import javax.swing.JComponent
import javax.swing.JLabel
import kotlin.test.Test
import kotlin.test.assertTrue

class BatikPlotComponentTest {
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
                plotSize = null,
                plotMaxWidth = null,
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

    }
}