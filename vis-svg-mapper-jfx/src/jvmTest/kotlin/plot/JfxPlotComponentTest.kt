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
import jetbrains.datalore.vis.swing.SceneMapperJfxPanel
import jetbrains.datalore.vis.swing.runOnFxThread
import javax.swing.JComponent
import javax.swing.JLabel
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertTrue

// ToDo: Make same test for LiveMap
class JfxPlotComponentTest {
    @Test
    @Ignore
    internal fun isDisposable_singlePlot() {
        val plotSpec = simplePlot(listOf(simplePointLayer()))
        val component = buildPlotFromRawSpecs(plotSpec)

        assertDisposable(component)
        (component as Disposable).dispose()
    }

    @Test
    @Ignore
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
        private val COMPONENT_FACTORY = { svg: SvgSvgElement -> SceneMapperJfxPanel(svg, stylesheets = emptyList()) }

        private val AWT_EDT_EXECUTOR = { runnable: () -> Unit ->
            runOnFxThread(runnable)
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