/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing.simple.batik

import jetbrains.datalore.base.logging.PortableLogging
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.swing.BatikMapperComponent
import jetbrains.datalore.vis.swing.BatikMessageCallback
import jetbrains.datalore.vis.swing.PlotComponentProvider
import javax.swing.SwingUtilities

class PlotComponentProviderBatik(
    processedSpec: MutableMap<String, Any>,
    var executor: (() -> Unit) -> Unit = AWT_EDT_EXECUTOR,
    computationMessagesHandler: ((List<String>) -> Unit)
) : PlotComponentProvider(
    processedSpec = processedSpec,
    svgComponentFactory = SVG_COMPONENT_FACTORY_BATIK,
    executor = executor,
    computationMessagesHandler = computationMessagesHandler
) {

    companion object {
        private val LOG = PortableLogging.logger(PlotComponentProviderBatik::class)

        private val SVG_COMPONENT_FACTORY_BATIK =
            { svg: SvgSvgElement -> BatikMapperComponent(svg, BATIK_MESSAGE_CALLBACK) }

        private val BATIK_MESSAGE_CALLBACK = object : BatikMessageCallback {
            override fun handleMessage(message: String) {
                LOG.info { message }
            }

            override fun handleException(e: Exception) {
                LOG.error(e) { "[Batik] Error while processing plot SVG." }
                if (e is RuntimeException) {
                    throw e
                }
                throw RuntimeException(e)
            }
        }

        private val AWT_EDT_EXECUTOR = { runnable: () -> Unit ->
            // Just invoke in the current thread.
            assert(SwingUtilities.isEventDispatchThread()) { "Not an Event Dispatch Thread (EDT)." }
            runnable.invoke()
        }
    }
}