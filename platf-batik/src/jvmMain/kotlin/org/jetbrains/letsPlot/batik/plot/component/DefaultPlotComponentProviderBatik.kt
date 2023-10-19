/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.batik.plot.component

import org.jetbrains.letsPlot.awt.plot.component.PlotSpecComponentProvider
import org.jetbrains.letsPlot.batik.plot.util.BatikMapperComponent
import org.jetbrains.letsPlot.batik.plot.util.BatikMessageCallback
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement

/**
 * Inherited by the IdeaPlotComponentProviderBatik class in IDEA plugin.
 */
open class DefaultPlotComponentProviderBatik(
    processedSpec: MutableMap<String, Any>,
    preserveAspectRatio: Boolean,
    executor: (() -> Unit) -> Unit,
    computationMessagesHandler: (List<String>) -> Unit
) : PlotSpecComponentProvider(
    processedSpec = processedSpec,
    preserveAspectRatio = preserveAspectRatio,
    svgComponentFactory = SVG_COMPONENT_FACTORY_BATIK,
    executor = executor,
    computationMessagesHandler = computationMessagesHandler
) {

    companion object {
        private val LOG = PortableLogging.logger(DefaultPlotComponentProviderBatik::class)

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
    }
}