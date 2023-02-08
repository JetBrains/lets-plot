/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing.batik

import jetbrains.datalore.base.logging.PortableLogging
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.swing.BatikMapperComponent
import jetbrains.datalore.vis.swing.BatikMessageCallback
import jetbrains.datalore.vis.swing.PlotSpecComponentProvider
import javax.swing.JComponent
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants

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

    /**
     * Override when used in IDEA plugin.
     * Use: JBScrollPane
     */
    override fun createScrollPane(plotComponent: JComponent): JScrollPane {
        return JScrollPane(
            plotComponent,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        ).apply {
            border = null
        }
    }

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